package potatoes.server.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Server Error"),
	INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "Invalid Input Value"),
	INVALID_TYPE_VALUE(400, "INVALID_TYPE_VALUE", "Invalid Type Value"),
	UNAUTHORIZED(401, "INVALID_TOKEN", "인증이 필요합니다"),

	// User
	EMAIL_DUPLICATION(400, "EMAIL_DUPLICATION", "이미 가입된 이메일입니다."),
	INVALID_CREDENTIALS(400, "INVALID_CREDENTIALS", "잘못된 이메일 또는 비밀번호입니다."),
	USER_NOT_FOUND(400, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
