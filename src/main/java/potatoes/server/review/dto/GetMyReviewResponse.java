package potatoes.server.review.dto;

import java.time.Instant;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetMyReviewResponse(
	Long reviewId,
	String title,
	String content,
	String reviewImage,
	String travelLocation,
	float starRating,
	Instant createdAt
) implements PagePolymorphic {
}
