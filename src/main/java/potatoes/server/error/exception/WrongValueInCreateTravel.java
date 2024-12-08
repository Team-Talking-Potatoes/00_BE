package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class WrongValueInCreateTravel extends CustomException {

	public WrongValueInCreateTravel(ErrorCode errorCode) {
		super(errorCode);
	}
}
