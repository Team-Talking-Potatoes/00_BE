package potatoes.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelUser;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {

	@Query("SELECT u FROM TravelUser u JOIN FETCH u.user WHERE u.travel = :travel")
	List<TravelUser> findAllByTravel(@Param("travel") Travel travel);

	int countByTravel(Travel travel);

	@Query("""
		    SELECT new potatoes.server.dto.GetMyTravelResponse(
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
		    SELECT new potatoes.server.dto.GetMyTravelResponse(
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
}
