package potatoes.server.travel.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {

	@Query("SELECT u FROM TravelUser u JOIN FETCH u.user WHERE u.travel = :travel")
	List<TravelUser> findAllByTravel(@Param("travel") Travel travel);

	@Query("""
		    SELECT new potatoes.server.travel.dto.GetMyTravelResponse(
		        t.id,
		        t.name,
		        t.maxTravelMateCount,
		        CAST(COUNT(tu2) AS int),
		        t.isDomestic,
		        t.travelLocation,
		        t.image,
		        CAST(t.startAt AS string),
		        CAST(t.endAt AS string)
		    )
		    FROM TravelUser tu
		    JOIN tu.travel t
		    LEFT JOIN TravelUser tu2 ON tu2.travel = t
		    WHERE tu.user.id = :userId AND tu.role = 'ORGANIZER'
		    GROUP BY t.id, t.name, t.maxTravelMateCount, t.isDomestic, t.travelLocation, t.image, t.startAt, t.endAt
		""")
	Page<GetMyTravelResponse> findMyTravels(Pageable pageable, Long userId);

	@Query("""
		SELECT new potatoes.server.travel.dto.GetMyTravelResponse(
		    t.id,
		    t.name,
		    t.maxTravelMateCount,
		    CAST(COUNT(tu2) AS int),
		    t.isDomestic,
		    t.travelLocation,
		    t.image,
		    CAST(t.startAt AS string),
		    CAST(t.endAt AS string)
		)
		FROM TravelUser tu
		JOIN tu.travel t
		LEFT JOIN TravelUser tu2 ON tu2.travel = t
		WHERE tu.user.id = :userId AND t.endAt < CURRENT_TIMESTAMP
		GROUP BY t.id, t.name, t.maxTravelMateCount, t.isDomestic, t.travelLocation, t.image, t.startAt, t.endAt
		""")
	Page<GetMyTravelResponse> findReviewableTravels(Pageable pageable, Long userId);

	@Query("""
		    SELECT new potatoes.server.travel.dto.GetMyTravelResponse(
		        t.id,
		        t.name,
		        t.maxTravelMateCount,
		        CAST(COUNT(tu2) AS int),
		        t.isDomestic,
		        t.travelLocation,
		        t.image,
		        CAST(t.startAt AS string),
		        CAST(t.endAt AS string)
		    )
		    FROM TravelUser tu
		    JOIN tu.travel t
		    LEFT JOIN TravelUser tu2 ON tu2.travel = t
		    WHERE tu.user.id = :userId
		    AND (:travelStatus = 'UPCOMING' AND t.endAt > CURRENT_TIMESTAMP
		         OR :travelStatus = 'PAST' AND t.endAt <= CURRENT_TIMESTAMP)
		    GROUP BY t.id, t.name, t.maxTravelMateCount, t.isDomestic, t.travelLocation, t.image, t.startAt, t.endAt
		""")
	Page<GetMyTravelResponse> findTravelsByStatus(
		Pageable pageable,
		Long userId,
		String travelStatus
	);

	@Query("""
		SELECT t FROM TravelUser t
		JOIN FETCH t.travel
		JOIN FETCH t.user
		WHERE t.createdAt > :date
		AND t.role = 'ORGANIZER'
		AND t.user.id = :userId
		""")
	List<TravelUser> findOrganizersCreatedAfter(@Param("date") Instant date, @Param("userId") Long userId);

	@Query("""
			SELECT tu FROM TravelUser tu
			WHERE tu.travel.id = :travelId
			AND tu.user.id = :userId
		""")
	Optional<TravelUser> findByTravelIdAndUserId(Long travelId, Long userId);

	boolean existsByTravelIdAndUserId(Long travelId, Long userId);

	long countByTravel(Travel travel);

	@Query("""
			SELECT t FROM TravelUser t
			JOIN FETCH t.travel
			JOIN FETCH t.user
			WHERE t.user.id = :userId
		""")
	List<TravelUser> findAllByUserId(@Param("userId") Long userId);

	@Query("""
			SELECT t FROM TravelUser t
			JOIN FETCH t.user
			WHERE t.travel.id = :travelId
			AND t.user.id = :userId
		""")
	Optional<TravelUser> findByTravelAndUserJoinFetchUser(@Param("travelId") Long travelId,
		@Param("userId") Long userId);

	@Query("""
		SELECT COUNT(t) FROM TravelUser t
		WHERE t.user.id = :userId
		AND t.role = 'ORGANIZER'
		""")
	long countTravelWhereUserIsHost(@Param("userId") Long userId);
}
