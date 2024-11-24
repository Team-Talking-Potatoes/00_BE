package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
}
