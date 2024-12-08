package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {
}
