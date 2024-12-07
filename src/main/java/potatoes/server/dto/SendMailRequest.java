package potatoes.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendMailRequest(
	@Email
	@NotBlank(message = "이메일은 필수입니다")

	String email
) {
}
