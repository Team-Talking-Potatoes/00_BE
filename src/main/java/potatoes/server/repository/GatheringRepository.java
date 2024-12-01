package potatoes.server.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.entity.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
	@Query("""
		    SELECT DISTINCT g FROM Gathering g
		    LEFT JOIN FETCH g.user
		    WHERE (:ids IS NULL OR g.id IN :ids)
		    AND (:type IS NULL OR g.type = :type)
		    AND (:location IS NULL OR g.location = :location)
		    AND (:startOfDay IS NULL OR g.dateTime >= :startOfDay)
		    AND (:endOfDay IS NULL OR g.dateTime < :endOfDay)
		    AND (:createdBy IS NULL OR g.createdBy = :createdBy)
		    AND (g.canceledAt IS NULL)
		""")
	Page<Gathering> findGatheringsWithFilters(
		@Param("ids") List<Long> ids,
		@Param("type") GatheringType type,
		@Param("location") LocationType location,
		@Param("startOfDay") Instant startOfDay,
		@Param("endOfDay") Instant endOfDay,
		@Param("createdBy") Long createdBy,
		Pageable pageable);

	Optional<Gathering> findByIdAndCanceledAtIsNull(Long id);
}
