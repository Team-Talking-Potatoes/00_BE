package potatoes.server.dto;

public record PopularUserResponse(
	Long userId,
	String profileImage,
	String nickName,
	int openTravelCount,
	long reviewCount,
	String hashTags
) {
}
