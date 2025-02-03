package potatoes.server.travel.bookmark.factory;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.entity.Travel;
import potatoes.server.user.entity.User;

@RequiredArgsConstructor
@Component
public class BookmarkFactory {

	private final BookmarkRepository bookmarkRepository;

	public void createBookmark(User user, Travel travel) {
		Bookmark bookmark = Bookmark.builder()
			.user(user)
			.travel(travel)
			.build();
		bookmarkRepository.save(bookmark);
	}

	public Bookmark getBookmark(Long userId, Long travelId){
		return bookmarkRepository.findByUserIdAndTravelId(userId,travelId);
	}

	public Boolean isUserParticipating(Optional<Long> userId, Long travelId) {
		return userId
			.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travelId))
			.orElse(null);
	}

	public Boolean isUserParticipating(Long userId, Long travelId) {
		return isUserParticipating(Optional.of(userId), travelId);
	}

	public void deleteBookmark(Bookmark bookmark) {
		bookmarkRepository.delete(bookmark);
	}
}
