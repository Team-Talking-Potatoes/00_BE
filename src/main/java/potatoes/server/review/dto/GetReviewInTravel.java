package potatoes.server.review.dto;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetReviewInTravel(
	Long reviewId,
	String title,
	String comment,
	String reviewImage,
	float starRating
)implements PagePolymorphic {
}
