package potatoes.server.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import potatoes.server.error.exception.CustomException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
		ErrorResponse response = ErrorResponse.of(errorCode, e.getBindingResult());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		ErrorResponse response = ErrorResponse.of(e);
		return ResponseEntity.status(400).body(response);
	}

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = ErrorResponse.of(errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse response = ErrorResponse.of(errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}
}
