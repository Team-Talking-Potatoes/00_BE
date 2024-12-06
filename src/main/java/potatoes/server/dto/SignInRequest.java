package potatoes.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
	@Email
	String email,
	@NotBlank
	String password
) {
}
