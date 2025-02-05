package potatoes.server.user.model;

public record UserModel(
	Long id,
	String email,
	String password,
	String name,
	String nickname,
	Integer birthDate,
	String contact,
	String description,
	String profileImage
) {
}
