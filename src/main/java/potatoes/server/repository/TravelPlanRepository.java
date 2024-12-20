package potatoes.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.Travel;
import potatoes.server.entity.TravelPlan;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {

	List<TravelPlan> findAllByTravel(Travel travel);
}
