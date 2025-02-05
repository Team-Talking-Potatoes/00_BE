package potatoes.server.review.dto;

public record TotalCountReviews(
	int totalReviews,
	int oneStarReviews,
	int twoStarReviews,
	int threeStarReviews,
	int fourStarReviews,
	int fiveStarReviews
) {
	public float calculateAverageRating() {
		float sumOfRatings = (oneStarReviews) +
			(twoStarReviews * 2) +
			(threeStarReviews * 3) +
			(fourStarReviews * 4) +
			(fiveStarReviews * 5);

		return totalReviews > 0 ? sumOfRatings / totalReviews : 0;
	}

	public TotalRatingResponse toResponse() {
		return new TotalRatingResponse(this, calculateAverageRating());
	}
}
