package potatoes.server.error.exception;

import static potatoes.server.error.ErrorCode.*;

public class BookmarkNotFound extends CustomException {

	public BookmarkNotFound() {
		super(BOOKMARK_NOT_FOUND);
	}
}
