package potatoes.server.dto;

import potatoes.server.entity.User;

public record GetUserResponse(
	Long id,
	String email,
	String name,
	String companyName,
	String image,
	long createdAt,
	long updatedAt
) {
	public static GetUserResponse fromEntity(User user) {
		return new GetUserResponse(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getCompanyName(),
			user.getImage(),
			user.getCreatedAt().toEpochMilli(),
			user.getUpdatedAt().toEpochMilli()
		);
	}
}
