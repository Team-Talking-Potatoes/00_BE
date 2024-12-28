package potatoes.server.service;

import static potatoes.server.error.ErrorCode.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ParticipantRole;
import potatoes.server.constant.TravelSortType;
import potatoes.server.constant.TravelStatus;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.dto.PageResponse;
import potatoes.server.dto.ParticipantResponse;
import potatoes.server.dto.SimpleTravelResponse;
import potatoes.server.dto.TravelDetailResponse;
import potatoes.server.dto.TravelListResponse;
import potatoes.server.dto.TravelPlanResponse;
import potatoes.server.dto.TravelSummaryResponse;
import potatoes.server.entity.Bookmark;
import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelPlan;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.repository.BookmarkRepository;
import potatoes.server.repository.TravelPlanRepository;
import potatoes.server.repository.TravelRepository;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.s3.S3UtilsProvider;

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
		long tripDuration = duration.toDays();
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
			.endAt(request.getStartAt().toInstant(ZoneOffset.UTC))
			.registrationEnd(request.getRegistrationEnd().toInstant(ZoneOffset.UTC))
			.tripDuration((int)tripDuration)
			.build();
		travelRepository.save(travel);

		List<TravelPlan> travelPlanList = request.getDetailTravel().stream()
			.map(details -> {
				String destinationFileName = s3.uploadFile(details.getDestinationImage());
				String destinationFileUrl = s3.getFileUrl(destinationFileName);
				return TravelPlan.builder()
					.travel(travel)
					.image(destinationFileUrl)
					.tripDay(details.getTripDay())
					.tripOrderNumber(details.getTripOrderNumber())
					.destination(details.getDestination())
					.description(details.getDescription())
					.build();
			}).toList();
		travelPlanRepository.saveAll(travelPlanList);

		TravelUser travelUser = TravelUser.builder()
			.role(ParticipantRole.ORGANIZER)
			.travel(travel)
			.user(user)
			.build();
		travelUserRepository.save(travelUser);
	}

	public TravelDetailResponse getDetails(Long travelId) {
		Travel travel = travelRepository.findById(travelId).orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
		List<TravelPlanResponse> travelPlanResponses = travelPlanRepository.findAllByTravel(travel).stream()
			.map(TravelPlanResponse::from)
			.toList();
		List<ParticipantResponse> participantResponses = travelUserRepository.findAllByTravel(travel).stream()
			.map(ParticipantResponse::from)
			.toList();
		return TravelDetailResponse.from(travel, travelPlanResponses, participantResponses);
	}

	public TravelListResponse getTravelList(int page, int size, Boolean isDomestic, String startAt, String endAt,
		TravelSortType sortOrder, String query) {
		Pageable pageable = createPageable(page, size, sortOrder);
		Page<Travel> travels = travelRepository.findTravels(isDomestic, startAt, endAt, query, pageable);
		List<TravelSummaryResponse> travelSummaryResponses = travels.getContent().stream()
			.map(travel -> {
				int travelUserCount = (int)travelUserRepository.countByTravel(travel);
				return TravelSummaryResponse.from(travel, travelUserCount);
			})
			.toList();
		return new TravelListResponse(travelSummaryResponses, travels.getTotalPages() + 1 > page, page);
	}

	private Pageable createPageable(int page, int size, TravelSortType sortOrder) {
		Sort sort = Sort.by(Sort.Direction.DESC, getSortField(sortOrder));
		return PageRequest.of(page, size, sort);
	}

	private String getSortField(TravelSortType sortOrder) {
		return switch (sortOrder) {
			// TODO - 조회수를 저장하는 로직이 없어 popular조건을 추가해야됨
			case recent, popular -> "createdAt";
			case registrationEnd -> "registrationEnd";
		};
	}

	public List<SimpleTravelResponse> getPopularTravels() {
		return travelRepository.findTop4ByOrderByIdDesc().stream()
			.map(travel -> {
				int currentTravelMate = (int)travelUserRepository.countByTravel(travel);
				return SimpleTravelResponse.from(travel, currentTravelMate);
			}).toList();
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
	public void deleteTravelByOrganizer(Long travelId, Long userId) {
		TravelUser travelUser = travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));

		if (travelUser.getRole() != ParticipantRole.ORGANIZER) {
			throw new WeGoException(INSUFFICIENT_TRAVEL_PERMISSION);
		}

		travelUserRepository.delete(travelUser);
		travelRepository.delete(travelUser.getTravel());
	}
}
