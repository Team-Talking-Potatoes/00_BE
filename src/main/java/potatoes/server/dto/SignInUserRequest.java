package potatoes.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInUserRequest(
	@NotBlank(message = "이메일는 필수입니다.")
	@Email(message = "이메일 형식으로 입력해주세요.")
	String email,
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password
) {
}
