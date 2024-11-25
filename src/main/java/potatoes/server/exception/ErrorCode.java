package potatoes.server.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// Common
	INTERNAL_SERVER_ERROR(500, "서버 에러 발생!"),
	UNAUTHORIZED(401, "Unauthorized"),
	FORBIDDEN(403, "Forbidden"),
	METHOD_NOT_ALLOWED(405, "Method not allowed"),

	// User
	EMAIL_DUPLICATION(400, "이미 가입된 이메일 입니다.");

	private final int status;
	private final String message;

	ErrorCode(final int status, final String message) {
		this.status = status;
		this.message = message;
	}
}
