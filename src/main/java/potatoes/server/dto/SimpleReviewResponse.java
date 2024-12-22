package potatoes.server.dto;

public record SimpleReviewResponse(
	Long reviewId,
	String nickname,
	String reviewImage
) {
}
