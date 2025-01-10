package potatoes.server.review.dto;

public record TotalCountReviews(
	int oneStarReviews,
	int twoStarReviews,
	int threeStarReviews,
	int fourStarReviews,
	int fiveStarReviews,
	int total
) {
	public float calculateAverageRating() {
		float sumOfRatings = (oneStarReviews * 1) +
			(twoStarReviews * 2) +
			(threeStarReviews * 3) +
			(fourStarReviews * 4) +
			(fiveStarReviews * 5);

		return total > 0 ? sumOfRatings / total : 0;
	}

	public TotalRatingResponse toResponse() {
		return new TotalRatingResponse(this, calculateAverageRating());
	}
}
