package potatoes.server.error.exception;

import static potatoes.server.error.ErrorCode.*;

public class TravelNotFound extends CustomException {

	public TravelNotFound() {
		super(TRAVEL_NOT_FOUND);
	}
}
