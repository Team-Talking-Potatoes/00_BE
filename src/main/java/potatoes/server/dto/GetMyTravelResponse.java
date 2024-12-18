package potatoes.server.dto;

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
) {
}
