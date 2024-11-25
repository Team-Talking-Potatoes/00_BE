package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import potatoes.server.dto.ErrorResponse;
import potatoes.server.exception.CustomException;
import potatoes.server.exception.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
		ErrorResponse response = ErrorResponse.builder()
			.code("VALIDATION_ERROR")
			.message("잘못된 요청입니다.")
			.build();

		for (FieldError fieldError : e.getFieldErrors()) {
			response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return ResponseEntity.badRequest().body(response);
	}

	@ResponseBody
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse body = ErrorResponse.builder()
			.code(String.valueOf(errorCode))
			.message(errorCode.getMessage())
			.build();
		return ResponseEntity.status(errorCode.getStatus())
			.body(body);
	}
}
