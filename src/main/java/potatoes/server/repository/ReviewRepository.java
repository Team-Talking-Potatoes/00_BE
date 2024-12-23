package potatoes.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.dto.GetMyReviewResponse;
import potatoes.server.dto.GetReviewResponse;
import potatoes.server.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("""
		SELECT new potatoes.server.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		    CASE 
		        WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review = r AND rl.user.id = :userId) 
		        THEN TRUE 
		        ELSE FALSE 
		    END,
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY r.createdAt DESC
		""")
	Page<GetReviewResponse> findAllByOrderByCreatedAtDesc(Pageable pageable, @Param("userId") Long userId);

	@Query("""
		SELECT new potatoes.server.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		    CASE 
		        WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review = r AND rl.user.id = :userId) 
		        THEN TRUE 
		        ELSE FALSE 
		    END,
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY (SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) DESC
		""")
	Page<GetReviewResponse> findAllByOrderByLikesCountDesc(Pageable pageable, @Param("userId") Long userId);

	@Query("""
		SELECT new potatoes.server.dto.GetMyReviewResponse(
		    r.id,
		    r.title,
		    MIN(ri.imageUrl),
		    t.travelLocation,
		    r.starRating,
		    r.createdAt
		)
		FROM Review r
		JOIN r.travel t
		LEFT JOIN r.reviewImages ri
		WHERE r.commenter.id = :userId
		GROUP BY r.id, r.title, t.travelLocation, r.starRating, r.createdAt
		""")
	Page<GetMyReviewResponse> findMyReviews(Pageable pageable, Long userId);

	@Query("""
		    SELECT r
		    FROM Review r
		    LEFT JOIN FETCH r.reviewImages
		    LEFT JOIN FETCH r.commenter
		    LEFT JOIN FETCH r.travel
		    WHERE r.id = :reviewId
		""")
	Review findReviewWithImagesAndCommenter(Long reviewId);

	@Query("SELECT r FROM Review r JOIN FETCH r.commenter ORDER BY r.createdAt DESC")
	List<Review> findRecentReviews(Pageable pageable);

	long countByTravelId(Long travelId);
}
