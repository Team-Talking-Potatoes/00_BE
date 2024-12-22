package potatoes.server.dto;

public record PopularUserResponse(
	String profileImage,
	String nickName,
	int openTravelCount,
	long reviewCount,
	String hashTags
) {
}
