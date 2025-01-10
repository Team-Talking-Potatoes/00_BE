package potatoes.server.review.dto;

public record SimpleReviewResponse(
	Long reviewId,
	String nickname,
	String reviewImage
) {
}
