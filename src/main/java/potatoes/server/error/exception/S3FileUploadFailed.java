package potatoes.server.error.exception;

import potatoes.server.error.ErrorCode;

public class S3FileUploadFailed extends CustomException {

	public S3FileUploadFailed() {
		super(ErrorCode.S3_FILE_UPLOAD_FAILED);
	}
}
