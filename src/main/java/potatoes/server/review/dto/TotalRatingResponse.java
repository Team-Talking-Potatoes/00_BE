package potatoes.server.review.dto;

public record TotalRatingResponse(
	TotalCountReviews reviews,
	float totalRating
) {
}
