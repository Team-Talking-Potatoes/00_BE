package potatoes.server.dto;

public record GetUserProfileResponse(
	Long userId,
	String email,
	String nickname,
	String image,
	String description
) {
}
