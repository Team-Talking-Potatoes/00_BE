package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class AlreadyExistsEmail extends CustomException {

	public AlreadyExistsEmail() {
		super(ErrorCode.EMAIL_DUPLICATION);
	}
}
