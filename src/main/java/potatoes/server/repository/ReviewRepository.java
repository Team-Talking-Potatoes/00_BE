package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
