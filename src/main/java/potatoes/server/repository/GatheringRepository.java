package potatoes.server.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
	@Query("SELECT g FROM Gathering g " +
		"WHERE (:ids IS NULL OR g.id IN :ids) " +
		"AND (:type IS NULL OR g.type = :type) " +
		"AND (:location IS NULL OR g.location LIKE CONCAT('%', :location, '%')) " +
		"AND (:startDate IS NULL OR g.dateTime >= :startDate) " +
		"AND (:endDate IS NULL OR g.dateTime < :endDate) " +
		"AND (:createdBy IS NULL OR g.createdBy = :createdBy)")
	Page<Gathering> findGatheringsWithConditions(
		@Param("ids") List<Long> ids,
		@Param("type") GatheringType type,
		@Param("location") String location,
		@Param("startDate") Instant startDate,
		@Param("endDate") Instant endDate,
		@Param("createdBy") Long createdBy,
		Pageable pageable
	);
}
