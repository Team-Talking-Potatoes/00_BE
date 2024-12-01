package potatoes.server.dto;

import lombok.Builder;
import potatoes.server.entity.User;

@Builder
public record UserInfo(
	Long id,
	String email,
	String name,
	String companyName,
	String image
) {
	public static UserInfo from(User entity) {
		return new UserInfo(
			entity.getId(),
			entity.getEmail(),
			entity.getName(),
			entity.getCompanyName(),
			entity.getImage()
		);
	}
}
