package potatoes.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerifyRequest(

	@Email
	@NotBlank(message = "이메일은 필수입니다")
	String email,
	@Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
	@NotBlank(message = "인증번호는 필수입니다")
	String verifyNumber
) {
}
