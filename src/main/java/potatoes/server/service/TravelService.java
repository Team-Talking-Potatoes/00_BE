package potatoes.server.service;

import static potatoes.server.error.ErrorCode.*;

import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ParticipantRole;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.entity.Travel;
import potatoes.server.entity.Bookmark;
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
import potatoes.server.utils.time.DateTimeUtils;

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

		int tripDuration = Period.between(request.getStartAt(), request.getEndAt()).getDays();
		if (tripDuration < 0 || request.getStartAt().isAfter(request.getEndAt())) {
			throw new WrongValueInCreateTravel(INVALID_TRAVEL_DATE);
		}

		for (CreateTravelRequest.DetailTravelRequest detailTravelRequest : request.getDetailTravel()) {
			if (detailTravelRequest.getTripDay() > tripDuration) {
				throw new WrongValueInCreateTravel(INVALID_TRAVEL_DETAIL_INFO);
			}
		}

		User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);

		String travelImageUrl = s3.uploadFile(request.getTravelImage());
		Travel travel = Travel.builder()
			.name(request.getTravelName())
			.description(request.getTravelDescription())
			.image(travelImageUrl)
			.expectedTripCost(request.getExpectedTripCost())
			.minTravelMateCount(request.getMinTravelMateCount())
			.maxTravelMateCount(request.getMaxTravelMateCount())
			.hashTags(request.getHashTags())
			.isDomestic(request.getIsDomestic())
			.travelLocation(request.getTravelLocation())
			.departureLocation(request.getDepartureLocation())
			.startAt(DateTimeUtils.localDateToInstant(request.getStartAt()))
			.endAt(DateTimeUtils.localDateToInstant(request.getEndAt()))
			.tripDuration(tripDuration)
			.build();
		travelRepository.save(travel);

		List<TravelPlan> travelPlanList = request.getDetailTravel().stream()
			.map(details -> {
				String destinationImageUrl = s3.uploadFile(details.getDestinationImage());
				return TravelPlan.builder()
					.travel(travel)
					.image(destinationImageUrl)
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
}
