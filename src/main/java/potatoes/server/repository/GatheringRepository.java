package potatoes.server.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Gathering;
import potatoes.server.entity.UserGathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
	@Query("""
		    SELECT DISTINCT g FROM Gathering g
		    LEFT JOIN FETCH g.user
		    WHERE (:ids IS NULL OR g.id IN :ids)
		    AND (:type IS NULL OR g.type = :type)
		    AND (:location IS NULL OR g.location = :location)
		    AND (:date IS NULL OR DATE(g.dateTime) = :date)
		    AND (:createdBy IS NULL OR g.createdBy = :createdBy)
		    AND (g.canceledAt IS NULL)
		""")
	Page<Gathering> findGatheringsWithFilters(
		@Param("ids") List<Long> ids,
		@Param("type") GatheringType type,
		@Param("location") String location,
		@Param("date") String date,
		@Param("createdBy") Long createdBy,
		Pageable pageable);

	@Query("""
		    SELECT ug
		    FROM UserGathering ug
		    JOIN FETCH ug.user
		    JOIN FETCH ug.gathering
		    WHERE ug.gathering.id = :gatheringId
		    ORDER BY ug.joinedAt
		""")
	Page<UserGathering> findParticipants(Long gatheringId, Pageable pageable);

	@Modifying
	@Query("UPDATE Gathering g SET g.canceledAt = :now WHERE g.id = :gatheringId AND g.createdBy = :userId")
	int cancelGathering(@Param("gatheringId") Long gatheringId, @Param("userId") Long userId,
		@Param("now") Instant now);
}
