package potatoes.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.Bookmark;
import potatoes.server.entity.Travel;
import potatoes.server.entity.User;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByUserAndTravel(User user, Travel travel);

	boolean existsByUserAndTravel(User user, Travel travel);
}
