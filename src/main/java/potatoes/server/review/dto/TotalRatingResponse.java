package potatoes.server.review.dto;

public record TotalRatingResponse(
	TotalCountReviews reviewRatings,
	float totalRating
) {
}
