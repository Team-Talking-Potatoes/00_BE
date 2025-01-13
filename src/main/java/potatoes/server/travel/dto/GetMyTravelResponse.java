package potatoes.server.travel.dto;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetMyTravelResponse(
	Long travelId,
	String travelName,
	int maxTravelMateCount,
	int currentTravelMateCount,
	boolean isDomestic,
	String travelLocation,
	String travelImage,
	String startAt,
	String endAt
) implements PagePolymorphic {
}
