package potatoes.server.review.dto;

import potatoes.server.utils.pagination.PagePolymorphic;

public record SimpleReviewResponse(
	Long reviewId,
	String nickname,
	String reviewImage
)implements PagePolymorphic {
}
