package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class UnauthorizedGatheringCancelException extends CustomException {

	public UnauthorizedGatheringCancelException() {
		super(ErrorCode.UNAUTHORIZED_GATHERING_CANCEL_EXCEPTION);
	}
}
