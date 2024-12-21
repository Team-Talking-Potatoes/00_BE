package potatoes.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
	boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
	int countAllByReviewId(Long id);
}
