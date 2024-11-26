package potatoes.server.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
	@Query("""
		    SELECT g FROM Gathering g
		    WHERE (:id IS NULL OR g.id IN :id)
		    AND (:type IS NULL OR g.type = :type)
		    AND (:location IS NULL OR g.location = :location)
		    AND (:startOfDay IS NULL OR g.dateTime >= :startOfDay)
		    AND (:endOfDay IS NULL OR g.dateTime <= :endOfDay)
		    AND (:createdBy IS NULL OR g.createdBy = :createdBy)
		""")
	Page<Gathering> findGatherings(
		String id,
		GatheringType type,
		String location,
		Instant startOfDay,
		Instant endOfDay,
		Long createdBy,
		Pageable pageable
	);
}
