package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class DuplicationEmail extends CustomException {

	public DuplicationEmail() {
		super(ErrorCode.EMAIL_DUPLICATION);
	}
}
