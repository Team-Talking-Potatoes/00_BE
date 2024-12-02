package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class NotParticipatingGatheringException extends CustomException {

	public NotParticipatingGatheringException() {
		super(ErrorCode.NOT_PARTICIPATING_GATHERING);
	}
}
