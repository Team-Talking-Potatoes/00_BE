package potatoes.server.dto;

import java.time.Instant;

import potatoes.server.entity.Gathering;
import potatoes.server.entity.Review;
import potatoes.server.entity.User;

public record GetReviewResponse(
	int id,
	int score,
	String comment,
	Instant createdAt,
	GatheringDto gathering,
	UserDto user
) {
	public record GatheringDto(
		int id,
		String type,
		String name,
		Instant dateTime,
		String location,
		String image
	) {
		public static GatheringDto from(Gathering gathering) {
			return new GatheringDto(
				gathering.getId().intValue(),
				gathering.getType().toString(),
				gathering.getName(),
				gathering.getDateTime(),
				gathering.getLocation(),
				gathering.getImage()
			);
		}
	}

	public record UserDto(
		int id,
		String name,
		String image
	) {
		public static UserDto from(User user) {
			return new UserDto(
				user.getId().intValue(),
				user.getName(),
				user.getImage()
			);
		}
	}

	public static GetReviewResponse from(Review review) {
		return new GetReviewResponse(
			review.getId().intValue(),
			review.getScore(),
			review.getComment(),
			review.getCreatedAt(),
			GatheringDto.from(review.getGathering()),
			UserDto.from(review.getUser())
		);
	}
}