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
		return UserInfo.builder()
			.id(entity.getId())
			.email(entity.getEmail())
			.name(entity.getName())
			.companyName(entity.getCompanyName())
			.image(entity.getImage())
			.build();
	}
}
