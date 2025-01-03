package potatoes.server.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {

	@Query("SELECT t FROM Travel t WHERE " +
		"(:isDomestic IS NULL OR t.isDomestic = :isDomestic) AND " +
		"(:startAt IS NULL OR t.startAt >= :startAt) AND " +
		"(:endAt IS NULL OR t.endAt <= :endAt) AND " +
		"(:query IS NULL OR t.name LIKE %:query%)")
	Page<Travel> findTravels(@Param("isDomestic") Boolean isDomestic,
		@Param("startAt") Instant startAt,
		@Param("endAt") Instant endAt,
		@Param("query") String query,
		Pageable pageable);

	List<Travel> findTop4ByOrderByIdDesc();
}
