package potatoes.server.dto;

import java.time.Instant;
import java.util.List;

public record GetDetailsReview(

	Long id,
	String title,
	String comment,
	float starRating,
	List<String> reviewImages,
	String nickname,
	int likesCount,
	String travelLocation,
	Instant createdAt
) {
}
