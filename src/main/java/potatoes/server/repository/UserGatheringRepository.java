package potatoes.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import potatoes.server.entity.UserGathering;

public interface UserGatheringRepository extends JpaRepository<UserGathering, Long> {
	@Query("""
		    SELECT ug
		    FROM UserGathering ug
		    JOIN FETCH ug.user
		    JOIN FETCH ug.gathering
		    WHERE ug.gathering.id = :gatheringId
		    ORDER BY ug.joinedAt
		""")
	Page<UserGathering> findParticipants(Long gatheringId, Pageable pageable);
}
