package potatoes.server.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.review.dto.GetMyReviewResponse;
import potatoes.server.review.dto.GetReviewInTravel;
import potatoes.server.review.dto.GetReviewResponse;
import potatoes.server.review.dto.SimpleReviewResponse;
import potatoes.server.review.dto.TotalCountReviews;
import potatoes.server.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("""
		SELECT new potatoes.server.review.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.comment,
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    r.commenter.profileImage,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		    CAST(NULL AS boolean),
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY r.createdAt DESC
		""")
	Page<GetReviewResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);

	@Query("""
		SELECT new potatoes.server.review.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.comment,
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    r.commenter.profileImage,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		    CAST(NULL AS boolean),
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY (SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) DESC
		""")
	Page<GetReviewResponse> findAllByOrderByLikesCountDesc(Pageable pageable);

	@Query("""
		SELECT new potatoes.server.review.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.comment,   
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    r.commenter.profileImage,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		     (CASE
		         WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review = r AND rl.user.id = :userId)
		         THEN true
		         ELSE false
		     END),
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY r.createdAt DESC
		""")
	Page<GetReviewResponse> findAllByOrderByCreatedAtDesc(Pageable pageable, @Param("userId") Long userId);

	@Query("""
		SELECT new potatoes.server.review.dto.GetReviewResponse(
		    r.id,
		    r.title,
		    r.comment,    
		    r.starRating,
		    COALESCE((SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review = r ORDER BY ri.id ASC LIMIT 1), ''),
		    r.commenter.nickname,
		    r.commenter.profileImage,
		    CAST((SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) AS int),
		     (CASE
		         WHEN EXISTS (SELECT 1 FROM ReviewLike rl WHERE rl.review = r AND rl.user.id = :userId)
		         THEN true
		         ELSE false
		     END),
		    r.travel.travelLocation,
		    r.createdAt
		)
		FROM Review r
		ORDER BY (SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review = r) DESC
		""")
	Page<GetReviewResponse> findAllByOrderByLikesCountDesc(Pageable pageable, @Param("userId") Long userId);

	@Query("""
		SELECT new potatoes.server.review.dto.GetMyReviewResponse(
		    r.id,
		    r.title,
		    r.comment,
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
		SELECT new potatoes.server.review.dto.GetReviewInTravel(
			r.id,
			r.title,
			r.comment,
		    MIN(ri.imageUrl),
			r.starRating
		)
		FROM Review r
		LEFT JOIN r.reviewImages ri
		WHERE r.travel.id = :travelId
		GROUP BY r.id, r.title, r.comment, r.starRating
		""")
	Page<GetReviewInTravel> findReviewByTravelId(Pageable pageable, Long travelId);

	@Query("""
		    SELECT r
		    FROM Review r
		    LEFT JOIN FETCH r.reviewImages
		    LEFT JOIN FETCH r.commenter
		    LEFT JOIN FETCH r.travel
		    WHERE r.id = :reviewId
		""")
	Review findReviewWithImagesAndCommenter(Long reviewId);

	@Query("""
		    SELECT NEW potatoes.server.review.dto.TotalCountReviews(
		        CAST((SELECT COUNT(r2) FROM Review r2 WHERE r2.travel.id = :travelId AND r2.starRating = 1.0) AS int),
		        CAST((SELECT COUNT(r2) FROM Review r2 WHERE r2.travel.id = :travelId AND r2.starRating = 2.0) AS int),
		        CAST((SELECT COUNT(r2) FROM Review r2 WHERE r2.travel.id = :travelId AND r2.starRating = 3.0) AS int),
		        CAST((SELECT COUNT(r2) FROM Review r2 WHERE r2.travel.id = :travelId AND r2.starRating = 4.0) AS int),
		        CAST((SELECT COUNT(r2) FROM Review r2 WHERE r2.travel.id = :travelId AND r2.starRating = 5.0) AS int),
		        CAST(COUNT(r) AS int)
		    )
		    FROM Review r
		    WHERE r.travel.id = :travelId
		""")
	TotalCountReviews countReviewsByRating(@Param("travelId") Long travelId);

	@Query("""
		SELECT NEW potatoes.server.review.dto.SimpleReviewResponse(
			r.id,
			u.nickname,
			MIN(ri.imageUrl)
		)
		FROM Review r
		JOIN r.commenter u
		LEFT JOIN r.reviewImages ri
		GROUP BY r.id, u.nickname
		ORDER BY r.createdAt DESC
		""")
	List<SimpleReviewResponse> findRecentReviews(Pageable pageable);

	@Query("SELECT r FROM Review r " +
		"JOIN TravelUser tu ON r.travel.id = tu.travel.id " +
		"WHERE tu.user.id = :userId " +
		"AND tu.role = 'ORGANIZER' " +
		"AND EXISTS (SELECT 1 FROM TravelUser tu2 WHERE tu2.travel = r.travel AND tu2.user.id = r.commenter.id) " +
		"ORDER BY r.createdAt DESC")
	List<Review> findTop3ReviewsByOrganizerIdOrderByCreatedAtDesc(@Param("userId") Long userId);

	long countByTravelId(Long travelId);
}
