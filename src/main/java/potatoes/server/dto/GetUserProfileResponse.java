package potatoes.server.dto;

public record GetUserProfileResponse(
	String email,
	String nickname,
	String image,
	String description
) {
}
