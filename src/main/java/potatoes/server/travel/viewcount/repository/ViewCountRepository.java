package potatoes.server.travel.viewcount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.travel.viewcount.entity.ViewCount;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {
}
