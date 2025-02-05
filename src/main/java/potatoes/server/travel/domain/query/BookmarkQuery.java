package potatoes.server.travel.domain.query;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.dto.GetMyTravelResponse;

@RequiredArgsConstructor
@Component
public class BookmarkQuery {

	private final BookmarkRepository bookmarkRepository;

	public Optional<Bookmark> findBookmark(Long userId, Long travelId) {
		return bookmarkRepository.findByUserIdAndTravelId(userId, travelId);
	}

	public Boolean isUserParticipating(Optional<Long> userId, Long travelId) {
		return userId
			.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travelId))
			.orElse(null);
	}

	public Boolean isUserParticipating(Long userId, Long travelId) {
		return isUserParticipating(Optional.of(userId), travelId);
	}

	public Page<GetMyTravelResponse> findTravelsByBookmark(Pageable pageable, Long userId) {
		return bookmarkRepository.findMyTravelsByBookmark(pageable, userId);
	}
}
