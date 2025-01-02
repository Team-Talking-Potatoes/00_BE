package potatoes.server.dto;

public record TotalRatingResponse(
	TotalCountReviews reviews,
	float totalRating
) {
}
