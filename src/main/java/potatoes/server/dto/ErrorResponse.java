package potatoes.server.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private final String code;
	private final String message;
	private final Map<String, String> parameter;

	@Builder
	public ErrorResponse(String code, String message, Map<String, String> parameter) {
		this.code = code;
		this.message = message;
		this.parameter = parameter != null ? parameter : new HashMap<>();
	}

	public void addValidation(String fieldName, String errorMessage) {
		this.parameter.put(fieldName, errorMessage);
	}
}
