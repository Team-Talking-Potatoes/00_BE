package potatoes.server.service;

import static potatoes.server.error.ErrorCode.*;

import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ParticipantRole;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelPlan;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.error.exception.WrongValueInCreateTravel;
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
			.registrationEnd(DateTimeUtils.localDateToInstant(request.getEndAt()))
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
}
