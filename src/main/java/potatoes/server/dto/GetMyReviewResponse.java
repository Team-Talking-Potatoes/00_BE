package potatoes.server.dto;

import java.time.Instant;

public record GetMyReviewResponse(
	Long reviewId,
	String title,
	String content,
	String imageUrl,
	String travelLocation,
	float starRating,
	Instant createdAt
) {
}
