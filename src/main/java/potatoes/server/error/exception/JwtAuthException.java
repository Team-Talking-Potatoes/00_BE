package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class JwtAuthException extends CustomException {

	public JwtAuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
