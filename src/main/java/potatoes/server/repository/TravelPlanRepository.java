package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.TravelPlan;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
}
