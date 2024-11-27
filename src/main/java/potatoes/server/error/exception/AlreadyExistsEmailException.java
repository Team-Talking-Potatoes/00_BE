package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class AlreadyExistsEmailException extends CustomException {

	public AlreadyExistsEmailException() {
		super(ErrorCode.EMAIL_DUPLICATION);
	}
}
