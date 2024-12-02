package potatoes.server.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("""
		SELECT DISTINCT r FROM Review r
		LEFT JOIN FETCH r.user
		LEFT JOIN FETCH r.gathering
		WHERE (:gatheringId IS NULL OR r.gathering.id = :gatheringId)
		AND (:userId IS NULL OR r.user.id = :userId)
		AND (:gatheringType IS NULL OR r.gathering.type = :gatheringType)
		AND (:location IS NULL OR r.gathering.location = :location)
		AND (:dateTime) IS NULL OR r.gathering.dateTime = :dateTime
		AND (:registrationEnd IS NULL OR r.gathering.registrationEnd = :registrationEnd)
		""")
	Page<Review> findReviewWithFilter(
		@Param("gathering") Long gatheringId,
		@Param("user") Long userId,
		@Param("gatheringType") GatheringType gatheringType,
		@Param("location") LocationType location,
		@Param("dateTime") Instant dateTime,
		@Param("registrationEnd") Instant registrationEnd,
		Pageable pageable);

	@Query("SELECT r FROM Review r JOIN FETCH r.gathering WHERE r.gathering.id =: gatheringId")
	List<Review> findAllByGathering(@Param("gatheringId") Long gatheringId);
}
