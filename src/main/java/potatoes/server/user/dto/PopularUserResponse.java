package potatoes.server.user.dto;

public record PopularUserResponse(
	Long userId,
	String profileImage,
	String nickName,
	int openTravelCount,
	long reviewCount,
	String hashTags
) {
}
