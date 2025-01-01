package potatoes.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	default Boolean existsByUserIdAndReviewIdWithNull(Long userId, Long reviewId) {
		if (userId == -1) {
			return null;
		}
		return existsByUserIdAndReviewId(userId, reviewId);
	}

	Boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

	Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);

	int countAllByReviewId(Long id);
}
