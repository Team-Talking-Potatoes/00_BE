package potatoes.server.dto;

import java.time.Instant;

import potatoes.server.entity.Gathering;
import potatoes.server.entity.Review;
import potatoes.server.entity.User;

public record CreateReviewResponse(
	Long id,
	Long userId,
	Long gatheringId,
	int score,
	String comment,
	Instant createdAt
) {
	public static CreateReviewResponse from(Gathering gathering, User user, Review review) {
		return new CreateReviewResponse(
			review.getId(),
			user.getId(),
			gathering.getId(),
			review.getScore(),
			review.getComment(),
			review.getCreatedAt()
		);
	}
}
