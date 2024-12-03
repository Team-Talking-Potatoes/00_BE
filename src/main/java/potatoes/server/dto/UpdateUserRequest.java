package potatoes.server.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
	@NotBlank(message = "회사이름은 필수입니다.")
	String companyName,
	String image
) {
}
