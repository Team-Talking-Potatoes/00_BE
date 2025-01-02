package potatoes.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
	@Email
	@NotBlank(message = "이메일은 필수입니다")
	String email,
	@NotBlank(message = "비밀번호는 필수입니다")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
		message = "비밀번호는 8~20자리이며, 영문/숫자/특수문자를 모두 포함해야 합니다."
	)
	String password,
	@NotBlank(message = "이름은 필수입니다")
	@Pattern(
		regexp = "^[a-zA-Z가-힣]+$",
		message = "이름은 한글과 영문만 입력 가능합니다, 공백은 불가능합니다"
	)
	String name,
	@NotBlank(message = "닉네임은 필수입니다")
	@Pattern(
		regexp = "^[a-zA-Z0-9가-힣]([a-zA-Z0-9가-힣]|(?<!\\s)\\s(?!\\s)){0,8}[a-zA-Z0-9가-힣]$",
		message = "닉네임은 2-10자의 한글, 영문, 숫자만 가능하며 시작과 끝은 공백이 불가능합니다. 연속된 공백은 불가능합니다"
	) String nickname,
	@NotBlank(message = "생일은 필수입니다")
	@Pattern(
		regexp = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$",
		message = "생일은 yyyyMMdd 형식으로 입력해주세요"
	)
	String birthDate,
	@NotBlank(message = "연락처는 필수입니다.")
	@Pattern(
		regexp = "^\\d{3}-\\d{4}-\\d{4}$",
		message = "연락처는 '000-0000-0000' 형식으로 입력해주세요"
	)
	String contact
) {
}
