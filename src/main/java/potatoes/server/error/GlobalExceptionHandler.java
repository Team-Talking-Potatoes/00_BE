package potatoes.server.error;

import static potatoes.server.error.ErrorCode.*;

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
		ErrorCode errorCode = INVALID_INPUT_VALUE;
		ErrorResponse response = ErrorResponse.of(errorCode, e.getBindingResult());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		ErrorResponse response = ErrorResponse.of(e);
		return ResponseEntity.status(INVALID_TYPE_VALUE.getStatus()).body(response);
	}

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = ErrorResponse.from(errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		ErrorResponse response = ErrorResponse.from(INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus()).body(response);
	}
}
