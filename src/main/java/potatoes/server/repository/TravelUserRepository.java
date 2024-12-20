package potatoes.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelUser;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {

	@Query("SELECT u FROM TravelUser u JOIN FETCH u.user WHERE u.travel = :travel")
	List<TravelUser> findAllByTravel(@Param("travel") Travel travel);
}
