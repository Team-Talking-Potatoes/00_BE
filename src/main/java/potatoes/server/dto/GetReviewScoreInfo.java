package potatoes.server.dto;

import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Review;

public record GetReviewScoreInfo(
	Long gathering,
	GatheringType type,
	float averageScore,
	int oneStars,
	int twoStars,
	int threeStars,
	int fourStars,
	int fiveStars
) {
	public static GetReviewScoreInfo from(Review review, float averageScore, int oneStars, int twoStars, int threeStars,
		int fourStars, int fiveStars) {
		return new GetReviewScoreInfo(
			review.getGathering().getId(),
			review.getGathering().getType(),
			averageScore,
			oneStars,
			twoStars,
			threeStars,
			fourStars,
			fiveStars
		);
	}
}
