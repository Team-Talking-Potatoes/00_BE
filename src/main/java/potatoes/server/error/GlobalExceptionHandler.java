package potatoes.server.error;

import static potatoes.server.error.ErrorCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;
import potatoes.server.error.exception.WeGoException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
		return ResponseEntity.status(INVALID_INPUT_VALUE.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		ErrorResponse response = ErrorResponse.of(e);
		return ResponseEntity.status(INVALID_TYPE_VALUE.getStatus()).body(response);
	}

	@ExceptionHandler(WeGoException.class)
	protected ResponseEntity<ErrorResponse> handleWeGoException(WeGoException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse response = ErrorResponse.from(errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	protected ResponseEntity<ErrorResponse> handleException(NoResourceFoundException e) {
		log.error("{}, {}", e.getClass(), e.getMessage());
		ErrorResponse response = ErrorResponse.from(INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.info("Internal Server Error", e);
		ErrorResponse response = ErrorResponse.from(INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus()).body(response);
	}
}
