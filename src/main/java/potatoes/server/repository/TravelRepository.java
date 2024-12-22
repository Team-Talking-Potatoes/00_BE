package potatoes.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {

	List<Travel> findTop4ByOrderByIdDesc();
}
