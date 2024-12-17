package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class TravelNotFound extends CustomException {

	public TravelNotFound() {
		super(ErrorCode.TRAVEL_NOT_FOUND);
	}
}
