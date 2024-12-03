package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class Unauthorized extends CustomException {

	public Unauthorized() {
		super(ErrorCode.UNAUTHORIZED);
	}
}
