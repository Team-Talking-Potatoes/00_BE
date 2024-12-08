package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class MailSendFailed extends CustomException {
	public MailSendFailed() {
		super(ErrorCode.MAIL_SEND_EXCEPTION);
	}
}
