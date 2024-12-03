package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class InvalidSignInInformation extends CustomException {

	public InvalidSignInInformation() {
		super(ErrorCode.INVALID_CREDENTIALS);
	}
}
