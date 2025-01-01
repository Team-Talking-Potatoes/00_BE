package potatoes.server.dto;

import java.time.Instant;

import potatoes.server.utils.pagination.PagePolymorphic;

public record GetReviewResponse(
	Long id,
	String title,
	float starRating,
	String reviewImage,
	String nickname,
	int likesCount,
	Boolean likesFlag,
	String travelLocation,
	Instant createdAt
) implements PagePolymorphic {
}
