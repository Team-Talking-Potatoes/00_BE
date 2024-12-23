package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class ReviewLikeNotFound extends CustomException {

	public ReviewLikeNotFound() {
		super(ErrorCode.REVIEW_LIKE_NOT_FOUND);
	}
}
