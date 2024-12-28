package potatoes.server.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import potatoes.server.entity.Review;
import potatoes.server.entity.ReviewImage;

public record GetDetailsReview(

	Long reviewId,
	Long travelId,
	String userProfileImage,
	String title,
	String comment,
	float starRating,
	List<String> reviewImages,
	String nickname,
	int likesCount,
	boolean likesFlag,
	String travelLocation,
	Instant createdAt
) {
	public static GetDetailsReview from(
		Review review,
		int reviewLikes,
		boolean likesFlag
	) {
		return new GetDetailsReview(
			review.getId(),
			review.getTravel().getId(),
			review.getCommenter().getProfileImage(),
			review.getTitle(),
			review.getComment(),
			review.getStarRating(),
			review.getReviewImages().stream()
				.map(ReviewImage::getImageUrl)
				.collect(Collectors.toList()),
			review.getCommenter().getNickname(),
			reviewLikes,
			likesFlag,
			review.getTravel().getTravelLocation(),
			review.getCreatedAt()
		);
	}
}
