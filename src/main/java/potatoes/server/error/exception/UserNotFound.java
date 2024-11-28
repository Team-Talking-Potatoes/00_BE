package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class UserNotFound extends CustomException {

	public UserNotFound() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}
