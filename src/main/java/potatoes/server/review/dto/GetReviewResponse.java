package potatoes.server.review.dto;

import java.time.Instant;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetReviewResponse(
	Long reviewId,
	String title,
	String content,
	float starRating,
	String reviewImage,
	String nickname,
	String profileImage,
	int likesCount,
	Boolean likesFlag,
	String travelLocation,
	Instant createdAt
) implements PagePolymorphic {
}
