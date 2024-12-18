package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class CookieNotFound extends CustomException {

	public CookieNotFound() {
		super(ErrorCode.COOKIE_NOT_FOUND);
	}
}
