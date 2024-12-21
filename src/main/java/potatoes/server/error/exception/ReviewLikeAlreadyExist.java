package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class ReviewLikeAlreadyExist extends CustomException {

	public ReviewLikeAlreadyExist() {
		super(ErrorCode.REVIEW_LIKE_ALREADY_EXIST);
	}
}
