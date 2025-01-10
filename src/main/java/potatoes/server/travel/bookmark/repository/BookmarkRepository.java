package potatoes.server.travel.bookmark.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.entity.Travel;
import potatoes.server.user.entity.User;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByUserAndTravel(User user, Travel travel);

	boolean existsByUserAndTravel(User user, Travel travel);

	@Query("""
		SELECT new potatoes.server.dto.GetMyTravelResponse(
		    t.id,
		    t.name,
		    t.maxTravelMateCount,
		    CAST(COUNT(tu2) AS int),
		    t.isDomestic,
		    t.travelLocation,
		    t.image,
		    CAST(t.startAt AS string),
		    CAST(t.endAt AS string)
		)
		FROM Travel t
		LEFT JOIN TravelUser tu2 ON tu2.travel = t
		JOIN Bookmark bk ON bk.travel = t
		WHERE bk.user.id = :userId
		GROUP BY t.id, t.name, t.maxTravelMateCount, t.isDomestic, t.travelLocation, t.image, t.startAt, t.endAt
		""")
	Page<GetMyTravelResponse> findMyTravelsByBookmark(Pageable pageable, Long userId);

	boolean existsByUserIdAndTravelId(Long userId, Long travelId);
}
