package potatoes.server.dto;

import potatoes.server.travel.entity.Travel;
import potatoes.server.utils.time.DateTimeUtils;

public record TravelSummaryResponse(
	Long travelId,
	String travelName,
	int expectedTripCost,
	int currentTravelMateCount,
	int minTravelMateCount,
	int maxTravelMateCount,
	boolean isDomestic,
	String location,
	String image,
	String startAt,
	String endAt,
	Boolean isBookmark
) {
	public static TravelSummaryResponse from(Travel travel, int currentTravelMateCount, Boolean isBookmark) {
		return new TravelSummaryResponse(
			travel.getId(),
			travel.getName(),
			travel.getExpectedTripCost(),
			currentTravelMateCount,
			travel.getMinTravelMateCount(),
			travel.getMaxTravelMateCount(),
			travel.isDomestic(),
			travel.getTravelLocation(),
			travel.getImage(),
			DateTimeUtils.getYearMonthDay(travel.getStartAt()),
			DateTimeUtils.getYearMonthDay(travel.getEndAt()),
			isBookmark
		);
	}
}
