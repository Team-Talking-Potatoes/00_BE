package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class PastGatheringException extends CustomException {

	public PastGatheringException() {
		super(ErrorCode.PAST_GATHERING_LEAVE_FORBIDDEN);
	}
}
