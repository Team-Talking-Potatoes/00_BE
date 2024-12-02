package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class UserNotFoundException extends CustomException {

	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}
