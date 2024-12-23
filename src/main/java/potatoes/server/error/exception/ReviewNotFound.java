package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class ReviewNotFound extends CustomException {

	public ReviewNotFound() {
		super(ErrorCode.REVIEW_NOT_FOUND);
	}
}
