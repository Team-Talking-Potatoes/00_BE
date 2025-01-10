package potatoes.server.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordCertification(
	@NotBlank(message = "삭제용 토큰은 필수입니다")
	String deleteUserToken
) {
}
