package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class AlreadyJoinedGatheringException extends CustomException {

	public AlreadyJoinedGatheringException() {
		super(ErrorCode.ALREADY_JOINED_GATHERING);
	}
}
