package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class MailVerifyNumberExpired extends CustomException {
	public MailVerifyNumberExpired() {
		super(ErrorCode.MAIL_VERIFY_NUMBER_EXPIRED);
	}
}
