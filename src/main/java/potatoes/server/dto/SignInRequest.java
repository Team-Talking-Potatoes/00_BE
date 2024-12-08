package potatoes.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignInRequest(
	@NotBlank(message = "이메일은 필수입니다")
	String email,
	@NotBlank(message = "비밀번호는 필수입니다")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
		message = "비밀번호는 8~20자리이며, 영문/숫자/특수문자를 모두 포함해야 합니다."
	)	String password
) {
}
