package potatoes.server.travel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelPlan;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {

	List<TravelPlan> findAllByTravel(Travel travel);
}
