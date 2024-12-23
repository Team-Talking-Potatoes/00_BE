package potatoes.server.dto;

import potatoes.server.entity.Travel;
import potatoes.server.utils.time.DateTimeUtils;

public record TravelSummaryResponse(
	Long travelId,
	String travelName,
	int expectedTripCost,
	int travelMateCount,
	int minTravelMateCount,
	int maxTravelMateCount,
	boolean isDomestic,
	String location,
	String image,
	String startAt,
	String endAt
) {
	public static TravelSummaryResponse from(Travel travel, int travelMateCount) {
		return new TravelSummaryResponse(
			travel.getId(),
			travel.getName(),
			travel.getExpectedTripCost(),
			travelMateCount,
			travel.getMinTravelMateCount(),
			travel.getMaxTravelMateCount(),
			travel.isDomestic(),
			travel.getTravelLocation(),
			travel.getImage(),
			DateTimeUtils.getYearMonthDay(travel.getStartAt()),
			DateTimeUtils.getYearMonthDay(travel.getEndAt())
		);
	}
}
