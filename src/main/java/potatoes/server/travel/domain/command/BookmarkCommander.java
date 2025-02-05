package potatoes.server.travel.domain.command;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.entity.Travel;
import potatoes.server.user.entity.User;

@RequiredArgsConstructor
@Component
public class BookmarkCommander {

	private final BookmarkRepository bookmarkRepository;

	public void createBookmark(User user, Travel travel) {
		Bookmark bookmark = Bookmark.builder()
			.user(user)
			.travel(travel)
			.build();
		bookmarkRepository.save(bookmark);
	}

	public void deleteBookmark(Bookmark bookmark) {
		bookmarkRepository.delete(bookmark);
	}

}
