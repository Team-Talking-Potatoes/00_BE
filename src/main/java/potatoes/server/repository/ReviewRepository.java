package potatoes.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import potatoes.server.dto.GetMyReviewResponse;
import potatoes.server.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

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
}
