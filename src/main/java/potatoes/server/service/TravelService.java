package potatoes.server.service;

import static potatoes.server.error.ErrorCode.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ParticipantRole;
import potatoes.server.constant.TravelStatus;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.dto.ParticipantResponse;
import potatoes.server.dto.TravelDetailResponse;
import potatoes.server.dto.TravelPlanResponse;
import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.dto.PageResponse;
import potatoes.server.entity.Bookmark;
import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelPlan;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.BookmarkNotFound;
import potatoes.server.error.exception.TravelNotFound;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.error.exception.WrongValueInCreateTravel;
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
			throw new WrongValueInCreateTravel(INVALID_TRAVEL_MATE_COUNT);
		}

		if (request.getHashTags().split("#").length > 5) {
			throw new WrongValueInCreateTravel(INVALID_TRAVEL_HASHTAGS_VALUE);
		}

		Duration duration = Duration.between(request.getStartAt(), request.getEndAt());
		long tripDuration = duration.toDays();
		if (tripDuration < 0 || request.getStartAt().isAfter(request.getEndAt())) {
			throw new WrongValueInCreateTravel(INVALID_TRAVEL_DATE);
		}

		for (CreateTravelRequest.DetailTravelRequest detailTravelRequest : request.getDetailTravel()) {
			if (detailTravelRequest.getTripDay() > tripDuration) {
				throw new WrongValueInCreateTravel(INVALID_TRAVEL_DETAIL_INFO);
			}
		}

		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);

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
		Travel travel = travelRepository.findById(travelId).orElseThrow(TravelNotFound::new);
		List<TravelPlanResponse> travelPlanResponses = travelPlanRepository.findAllByTravel(travel).stream()
			.map(TravelPlanResponse::from)
			.toList();
		List<ParticipantResponse> participantResponses = travelUserRepository.findAllByTravel(travel).stream()
			.map(ParticipantResponse::from)
			.toList();
		return TravelDetailResponse.from(travel, travelPlanResponses, participantResponses);
	}
	
	@Transactional
	public void addBookmark(Long userId, Long travelId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
		Travel travel = travelRepository.findById(travelId).orElseThrow(TravelNotFound::new);
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
		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
		Travel travel = travelRepository.findById(travelId).orElseThrow(TravelNotFound::new);

		Bookmark bookmark = bookmarkRepository.findByUserAndTravel(user, travel)
			.orElseThrow(BookmarkNotFound::new);
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
}
