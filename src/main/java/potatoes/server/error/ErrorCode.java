package potatoes.server.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Server Error"),
	INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "Invalid Input Value"),
	INVALID_TYPE_VALUE(400, "INVALID_TYPE_VALUE", "Invalid Type Value"),
	UNAUTHORIZED(403, "INVALID_TOKEN", "인가가 필요합니다"),
	TOKEN_NOT_FOUND(400, "TOKEN_NOT_FOUND", "accessToken을 찾을 수 없습니다"),
	COOKIE_NOT_FOUND(400, "COOKIE_NOT_FOUND", "쿠키를 찾을 수 없습니다"),
	VERIFY_NUMBER_NOT_VALID(400, "VERIFY_NUMBER_NOT_VALID", "번호가 일치하지 않습니다."),
	VERIFY_NUMBER_EXPIRED(400, "VERIFY_NUMBER_EXPIRED", "번호의 유효시간이 끝났거나 존재하지 않습니다"),

	// User
	EMAIL_DUPLICATION(400, "EMAIL_DUPLICATION", "이미 가입된 이메일입니다."),
	USER_NOT_FOUND(400, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
	INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "잘못된 이메일 또는 비밀번호입니다."),
	PASSWORD_MISMATCH(400, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),

	// Mail(SMTP)
	MAIL_SEND_EXCEPTION(502, "MAIL_SEND_EXCEPTION", "메일을 전송하지 못했습니다"),

	// Tavel
	TRAVEL_NOT_FOUND(400, "TRAVEL_NOT_FOUND", "존재하지 않는 여행입니다."),
	INVALID_TRAVEL_MATE_COUNT(400, "INVALID_TRAVEL_MATE_COUNT", "참가자 최소 인원은 최대 인원을 초과할 수 없습니다."),
	INVALID_TRAVEL_DETAIL_INFO(400, "INVALID_TRAVEL_DETAIL_INFO", "상세 일정의 날은 전체 일정의 날을 초과할 수 없습니다."),
	INVALID_TRAVEL_DATE(400, "INVALID_TRAVEL_DATE", "출발일자/종료일자 값이 잘못되었습니다."),
	INVALID_TRAVEL_HASHTAGS_VALUE(400, "INVALID_TRAVEL_HASHTAGS_VALUE", "해시태그 최대 횟수를 초과하였습니다."),
	INSUFFICIENT_TRAVEL_PERMISSION(400, "INSUFFICIENT_TRAVEL_PERMISSION", "여행의 권한이 부족합니다"),
	NOT_PARTICIPATED_TRAVEL(400, "NOT_PRATICIPATED_TRAVEL", "참여하지 않은 여행입니다"),
	ALREADY_PARTICIPATED_TRAVEL(400, "ALREADY_PARTICIPATED_TRAVEL", "이미 참여한 여행입니다"),

	// Bookmark
	BOOKMARK_ALREADY_EXIST(400, "BOOKMARK_ALREADY_EXIST", "이미 등록한 북마크입니다."),
	BOOKMARK_NOT_FOUND(400, "BOOKMARK_NOT_FOUND", "등록되지 않은 북마크입니다."),

	// Image
	INVALID_FILE_FORMAT(400, "INVALID_FILE_FORMAT", "잘못된 형식의 파일입니다"),
	S3_FILE_UPLOAD_FAILED(500, "S3_FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),

	// Review
	REVIEW_NOT_FOUND(400, "REVIEW_NOT_FOUND", "존재하지 않는 리뷰입니다."),
	REVIEW_LIKE_NOT_FOUND(400, "REVIEW_LIKE_NOT_FOUND", "좋아요한 리뷰를 찾을 수 없습니다"),
	REVIEW_LIKE_ALREADY_EXIST(400, "REVIEW_LIKE_ALREADY_EXIST", "이미 좋아요한 리뷰입니다"),

	// Chat
	CHAT_NOT_FOUND(400, "CHAT_NOT_FOUND", "존재하지 않는 채팅입니다."),
	UNABLE_TO_JOIN_CHAT(400, "UNABLE_TO_JOIN_CHAT", "채팅에 참여할 수 없습니다."),
	ALREADY_JOINED_CHAT(400, "ALREADY_JOINED_CHAT", "이미 참여한 채팅입니다."),
	
	// STOMP
	STOMP_SUBSCRIBE_FAILED(400, "STOMP_SUBSCRIBE_FAILED", "채팅방 구독 실패");

	private final int status;
	private final String code;
	private final String message;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
