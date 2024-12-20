package potatoes.server.dto;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetMyTravelResponse(
	Long travelId,
	String travelName,
	int maxTravelMateCount,
	int currentTravelMateCount,
	boolean isDomestic,
	String location,
	String image,
	String startAt,
	String endAt
) implements PagePolymorphic {
}
