package potatoes.server.user.dto;

import potatoes.server.user.entity.User;

public record GetUserProfileResponse(
	Long userId,
	String email,
	String nickname,
	String profileImage,
	String description
) {
	public static GetUserProfileResponse from(User user) {
		return new GetUserProfileResponse(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getProfileImage(),
			user.getDescription()
		);
	}
}
