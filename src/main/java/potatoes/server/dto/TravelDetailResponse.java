package potatoes.server.dto;

import java.util.List;

import potatoes.server.entity.Travel;
import potatoes.server.utils.time.DateTimeUtils;

public record TravelDetailResponse(
	Long travelId,
	String travelName,
	String description,
	String image,
	int expectedTripCost,
	int currentTravelMateCount,
	int minTravelMateCount,
	int maxTravelMateCount,
	String hashTags,
	boolean isDomestic,
	String travelLocation,
	String departureLocation,
	String startAt,
	String endAt,
	String registrationEnd,
	int tripDuration,
	List<TravelPlanResponse> travelPlan,
	List<ParticipantResponse> participant,
	Boolean participationFlag
) {
	public static TravelDetailResponse from(Travel travel, List<TravelPlanResponse> travelPlan,
		List<ParticipantResponse> participant, Boolean participationFlag) {
		return new TravelDetailResponse(
			travel.getId(),
			travel.getName(),
			travel.getDescription(),
			travel.getImage(),
			travel.getExpectedTripCost(),
			participant.size(),
			travel.getMinTravelMateCount(),
			travel.getMaxTravelMateCount(),
			travel.getHashTags(),
			travel.isDomestic(),
			travel.getTravelLocation(),
			travel.getDepartureLocation(),
			DateTimeUtils.getYearMonthDay(travel.getStartAt()),
			DateTimeUtils.getYearMonthDay(travel.getEndAt()),
			DateTimeUtils.getYearMonthDay(travel.getRegistrationEnd()),
			travel.getTripDuration(),
			travelPlan,
			participant,
			participationFlag
		);
	}
}
