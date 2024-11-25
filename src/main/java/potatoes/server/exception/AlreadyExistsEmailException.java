package potatoes.server.exception;

public class AlreadyExistsEmailException extends CustomException {

	public AlreadyExistsEmailException() {
		super(ErrorCode.EMAIL_DUPLICATION);
	}
}
