package potatoes.server.dto;

import potatoes.server.user.entity.User;

public record ParticipantsInfoResponse(
	String user,
	String email,
	String description,
	long travelCount,
	String profileImage
) {
	public static ParticipantsInfoResponse of(User user, long travelCount) {
		return new ParticipantsInfoResponse(
			user.getNickname(),
			user.getEmail(),
			user.getDescription(),
			travelCount,
			user.getProfileImage()
		);
	}
}
