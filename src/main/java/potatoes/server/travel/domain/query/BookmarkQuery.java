package potatoes.server.travel.domain.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.dto.GetMyTravelResponse;

@RequiredArgsConstructor
@Component
public class BookmarkQuery {

	private final BookmarkRepository bookmarkRepository;

	public Page<GetMyTravelResponse> findTravelsByBookmark(Pageable pageable, Long userId) {
		return bookmarkRepository.findMyTravelsByBookmark(pageable, userId);
	}
}
