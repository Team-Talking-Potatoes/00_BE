package potatoes.server.travel.service;

import static potatoes.server.utils.error.ErrorCode.*;
import static potatoes.server.utils.time.DateTimeUtils.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.chat.entity.Chat;
import potatoes.server.chat.repository.ChatRepository;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.dto.PageResponse;
import potatoes.server.dto.ParticipantResponse;
import potatoes.server.dto.SimpleTravelResponse;
import potatoes.server.dto.TravelDetailResponse;
import potatoes.server.dto.TravelListResponse;
import potatoes.server.dto.TravelPlanResponse;
import potatoes.server.dto.TravelSummaryResponse;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.entity.User;
import potatoes.server.user.repository.UserRepository;
import potatoes.server.utils.constant.ParticipantRole;
import potatoes.server.utils.constant.TravelSortType;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.error.exception.WeGoException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TravelService {

	private final UserRepository userRepository;
	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;
	private final TravelUserRepository travelUserRepository;
	private final BookmarkRepository bookmarkRepository;
	private final ChatRepository chatRepository;
	private final S3UtilsProvider s3;

	@Transactional
	public void createTravel(Long userId, CreateTravelRequest request) {

		if (request.getMinTravelMateCount() > request.getMaxTravelMateCount()) {
			throw new WeGoException(INVALID_TRAVEL_MATE_COUNT);
		}

		if (request.getHashTags().split("#").length > 5) {
			throw new WeGoException(INVALID_TRAVEL_HASHTAGS_VALUE);
		}

		Duration duration = Duration.between(request.getStartAt(), request.getEndAt());
		long tripDuration = duration.toDays() + 1;
		if (tripDuration < 0 || request.getStartAt().isAfter(request.getEndAt())) {
			throw new WeGoException(INVALID_TRAVEL_DATE);
		}

		for (CreateTravelRequest.DetailTravelRequest detailTravelRequest : request.getDetailTravel()) {
			if (detailTravelRequest.getTripDay() > tripDuration) {
				throw new WeGoException(INVALID_TRAVEL_DETAIL_INFO);
			}
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));

		String travelFileName = s3.uploadFile(request.getTravelImage());
		String travelFileUrl = s3.getFileUrl(travelFileName);

		Travel travel = Travel.builder()
			.name(request.getTravelName())
			.description(request.getTravelDescription())
			.image(travelFileUrl)
			.expectedTripCost(request.getExpectedTripCost())
			.minTravelMateCount(request.getMinTravelMateCount())
			.maxTravelMateCount(request.getMaxTravelMateCount())
			.hashTags(request.getHashTags())
			.isDomestic(request.getIsDomestic())
			.travelLocation(request.getTravelLocation())
			.departureLocation(request.getDepartureLocation())
			.startAt(request.getStartAt().toInstant(ZoneOffset.UTC))
			.endAt(request.getEndAt().toInstant(ZoneOffset.UTC))
			.registrationEnd(request.getRegistrationEnd().toInstant(ZoneOffset.UTC))
			.tripDuration((int)tripDuration)
			.build();
		travelRepository.save(travel);

		List<TravelPlan> travelPlanList = request.getDetailTravel().stream()
			.map(details -> {
				String destinationFileUrl = null;
				if (details.getDestinationImage() != null) {
					String destinationFileName = s3.uploadFile(details.getDestinationImage());
					destinationFileUrl = s3.getFileUrl(destinationFileName);
				}
				return TravelPlan.builder()
					.travel(travel)
					.image(destinationFileUrl)
					.tripDay(details.getTripDay())
					.tripOrderNumber(details.getTripOrderNumber())
					.destination(details.getDestination())
					.description(details.getDescription())
					.build();
			})
			.toList();
		travelPlanRepository.saveAll(travelPlanList);

		TravelUser travelUser = TravelUser.builder()
			.role(ParticipantRole.ORGANIZER)
			.travel(travel)
			.user(user)
			.build();

		Chat chat = Chat.builder()
			.name(travel.getName())
			.host(user)
			.travel(travel)
			.currentMemberCount(1)
			.maxMemberCount(travel.getMaxTravelMateCount())
			.build();
		chatRepository.save(chat);

		travelUserRepository.save(travelUser);
	}

	public TravelDetailResponse getDetails(Long travelId, Optional<Long> userId) {
		Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		Boolean participationFlag = userId
			.map(uid -> travelUserRepository.existsByTravelIdAndUserId(travelId, uid))
			.orElse(null);

		Boolean bookmarkFlag = userId
			.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travelId))
			.orElse(null);

		List<TravelPlanResponse> travelPlanResponses = travelPlanRepository.findAllByTravel(travel).stream()
			.map(TravelPlanResponse::from)
			.toList();
		List<ParticipantResponse> participantResponses = travelUserRepository.findAllByTravel(travel).stream()
			.map(ParticipantResponse::from)
			.toList();
		return TravelDetailResponse.from(travel, travelPlanResponses, participantResponses, participationFlag,
			bookmarkFlag);
	}

	public TravelListResponse getTravelList(
		int page, int size, Boolean isDomestic, String startAt, String endAt,
		TravelSortType sortOrder, String query, Optional<Long> userId
	) {
		Pageable pageable = createPageable(page, size, sortOrder);

		Instant parsedStartAt = startAt != null ? parseYearMonthDay(startAt) : null;
		Instant parsedEndAt = endAt != null ? parseYearMonthDay(endAt) : null;

		Page<Travel> travels = travelRepository.findTravels(
			isDomestic,
			parsedStartAt,
			parsedEndAt,
			query,
			pageable
		);

		List<TravelSummaryResponse> travelSummaryResponses = travels.getContent().stream()
			.map(travel -> {
				int currentTravelMateCount = (int)travelUserRepository.countByTravel(travel);

				Boolean isBookmark = userId
					.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travel.getId()))
					.orElse(null);

				return TravelSummaryResponse.from(travel, currentTravelMateCount, isBookmark);
			})
			.toList();
		return new TravelListResponse(travelSummaryResponses, travels.hasNext(), page + 1);
	}

	private Pageable createPageable(int page, int size, TravelSortType sortOrder) {
		Sort sort = Sort.by(
			sortOrder == TravelSortType.registrationEnd
				? Sort.Direction.ASC
				: Sort.Direction.DESC,
			getSortField(sortOrder)
		);
		return PageRequest.of(page, size, sort);
	}

	private String getSortField(TravelSortType sortOrder) {
		return switch (sortOrder) {
			// TODO - 조회수를 저장하는 로직이 없어 popular조건을 추가해야됨
			case recent, popular -> "createdAt";
			case registrationEnd -> "registrationEnd";
		};
	}

	public List<SimpleTravelResponse> getPopularTravels(int page, int size, Optional<Long> userId) {
		Pageable pageable = PageRequest.of(page - 1, size);
		return travelRepository.findAllByOrderByIdDesc(pageable).stream()
			.map(travel -> {
				int currentTravelMate = (int)travelUserRepository.countByTravel(travel);

				Boolean isBookmark = userId
					.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travel.getId()))
					.orElse(null);

				return SimpleTravelResponse.from(travel, currentTravelMate, isBookmark);
			})
			.toList();
	}

	@Transactional
	public void addBookmark(Long userId, Long travelId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
		Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
		if (bookmarkRepository.existsByUserAndTravel(user, travel)) {
			return;
		}

		Bookmark bookmark = Bookmark.builder()
			.user(user)
			.travel(travel)
			.build();
		bookmarkRepository.save(bookmark);
	}

	@Transactional
	public void deleteBookmark(Long userId, Long travelId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
		Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		Bookmark bookmark = bookmarkRepository.findByUserAndTravel(user, travel)
			.orElseThrow(() -> new WeGoException(BOOKMARK_NOT_FOUND));
		bookmarkRepository.delete(bookmark);
	}

	public PageResponse<GetMyTravelResponse> getMyTravels(int page, int size, Long userId) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findMyTravels(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getTravelsByStatus(
		int page, int size, Long userId, TravelStatus travelStatus
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findTravelsByStatus(request, userId,
			travelStatus.name());
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getMyTravelsByBookmark(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = bookmarkRepository.findMyTravelsByBookmark(request, userId);
		return PageResponse.from(findTravels);
	}

	public PageResponse<GetMyTravelResponse> getReviewableMyTravels(
		int page, int size, Long userId
	) {
		PageRequest request = PageRequest.of(page, size);
		Page<GetMyTravelResponse> findTravels = travelUserRepository.findReviewableTravels(request, userId);
		return PageResponse.from(findTravels);
	}

	@Transactional
	public void participateInTravel(Long travelId, Long userId) {
		Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		User user = userRepository.findById(userId).orElseThrow(() -> new WeGoException(USER_NOT_FOUND));

		boolean existsCheckFlag = travelUserRepository.existsByTravelIdAndUserId(travelId, userId);

		if (existsCheckFlag) {
			throw new WeGoException(ALREADY_PARTICIPATED_TRAVEL);
		}

		TravelUser travelUser = TravelUser.builder()
			.role(ParticipantRole.ATTENDEE)
			.user(user)
			.travel(travel)
			.build();

		travelUserRepository.save(travelUser);
	}

	@Transactional
	public void deleteTravelByOrganizer(Long travelId, Long userId) {
		TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		if (travelUser.getRole() != ParticipantRole.ORGANIZER) {
			throw new WeGoException(INSUFFICIENT_TRAVEL_PERMISSION);
		}

		travelUserRepository.delete(travelUser);
		travelRepository.delete(travelUser.getTravel());
	}

	@Transactional
	public void deleteTravelByAttendee(Long travelId, Long userId) {
		TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		if (travelUser.getRole() != ParticipantRole.ATTENDEE) {
			throw new WeGoException(NOT_PARTICIPATED_TRAVEL);
		}

		travelUserRepository.delete(travelUser);
	}

}
