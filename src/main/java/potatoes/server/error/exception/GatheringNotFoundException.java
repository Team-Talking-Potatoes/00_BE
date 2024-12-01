package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class GatheringNotFoundException extends CustomException {

	public GatheringNotFoundException() {
		super(ErrorCode.GATHERING_NOT_FOUND_OR_CANCELED);
	}
}
