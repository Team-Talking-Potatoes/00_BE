package potatoes.server.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.review.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	default Boolean existsByUserIdAndReviewIdWithNull(Optional<Long> userId, Long reviewId) {
		return userId
			.map(uid -> existsByUserIdAndReviewId(uid, reviewId))
			.orElse(null);
	}

	Boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

	Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);

	int countAllByReviewId(Long id);
}
