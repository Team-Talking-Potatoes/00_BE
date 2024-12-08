package potatoes.server.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Server Error"),
	INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "Invalid Input Value"),
	INVALID_TYPE_VALUE(400, "INVALID_TYPE_VALUE", "Invalid Type Value"),
	UNAUTHORIZED(401, "INVALID_TOKEN", "인증이 필요합니다"),
	AUTHORIZATION_HEADER_NULL(400, "AUTHORIZATION_HEADER_NULL", "인증 헤더가 null입니다."),
	INVALID_TOKEN_PREFIX(400, "INVALID_TOKEN_PREFIX", "Bearer값이 아닙니다."),

	// User
	EMAIL_DUPLICATION(400, "EMAIL_DUPLICATION", "이미 가입된 이메일입니다."),
	USER_NOT_FOUND(400, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
	INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "잘못된 이메일 또는 비밀번호입니다."),

	// Mail(SMTP)
	MAIL_VERIFY_NUMBER_NOT_VALID(400, "NUMBER_NOT_VALID", "번호가 일치하지 않습니다."),
	MAIL_VERIFY_NUMBER_EXPIRED(400, "NUMBER_EXPIRED", "번호의 유효시간이 끝났습니다"),
	MAIL_SEND_EXCEPTION(502, "MAIL_SEND_EXCEPTION", "메일을 전송하지 못했습니다"),

	// Gathering
	NOT_PARTICIPATING_GATHERING(400, "ALREADY_PARTICIPATING_GATHERING", "참여하지 않은 모임입니다"),
	ALREADY_JOINED_GATHERING(400, "ALREADY_JOINED_GATHERING", "이미 참여한 모임입니다."),
	GATHERING_NOT_FOUND_OR_CANCELED(400, "GATHERING_NOT_FOUND_OR_CANCELED", "존재하지 않는 모임이거나 이미 취소된 모임입니다."),
	UNAUTHORIZED_GATHERING_CANCEL_EXCEPTION(403, "UNAUTHORIZED_GATHERING_CANCEL_EXCEPTION", "모임 취소 권한이 없습니다"),
	PAST_GATHERING_LEAVE_FORBIDDEN(404, "PAST_GATHERING_LEAVE_FORBIDDEN", "이미 지난 모임은 참여 취소가 불가능합니다."),

	// Image
	INVALID_FILE_FORMAT(400, "INVALID_FILE_FORMAT", "잘못된 형식의 파일입니다"),
	S3_FILE_UPLOAD_FAILED(500, "S3_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다.");

	private final int status;
	private final String code;
	private final String message;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
