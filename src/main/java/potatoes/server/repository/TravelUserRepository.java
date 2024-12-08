package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.TravelUser;

public interface TravelUserRepository extends JpaRepository<TravelUser, Long> {
}
