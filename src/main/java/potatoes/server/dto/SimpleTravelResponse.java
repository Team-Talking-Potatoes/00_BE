package potatoes.server.dto;

import potatoes.server.travel.entity.Travel;
import potatoes.server.utils.time.DateTimeUtils;

public record SimpleTravelResponse(
	Long travelId,
	String travelName,
	int maxTravelMateCount,
	int currentTravelMateCount,
	boolean isDomestic,
	Boolean isBookmark,
	String location,
	String image,
	String startAt,
	String endAt
) {
	public static SimpleTravelResponse from(Travel travel, int currentTravelMateCount, Boolean isBookmark) {
		return new SimpleTravelResponse(
			travel.getId(),
			travel.getName(),
			travel.getMaxTravelMateCount(),
			currentTravelMateCount,
			travel.isDomestic(),
			isBookmark,
			travel.getTravelLocation(),
			travel.getImage(),
			DateTimeUtils.getYearMonthDay(travel.getStartAt()),
			DateTimeUtils.getYearMonthDay(travel.getEndAt())
		);
	}
}
