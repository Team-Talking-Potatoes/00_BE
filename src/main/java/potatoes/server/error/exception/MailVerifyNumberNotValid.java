package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class MailVerifyNumberNotValid extends CustomException {

	public MailVerifyNumberNotValid() {
		super(ErrorCode.MAIL_VERIFY_NUMBER_NOT_VALID);
	}
}
