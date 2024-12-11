package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class PasswordMismatch extends CustomException {

	public PasswordMismatch() {
		super(ErrorCode.PASSWORD_MISMATCH);
	}
}
