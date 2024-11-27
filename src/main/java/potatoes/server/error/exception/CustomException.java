package potatoes.server.error.exception;

import lombok.Getter;
import potatoes.server.error.ErrorCode;

@Getter
public abstract class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
