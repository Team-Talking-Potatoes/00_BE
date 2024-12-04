package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class InvalidFileFormat extends CustomException {

	public InvalidFileFormat() {
		super(ErrorCode.INVALID_FILE_FORMAT);
	}
}
