package potatoes.server.dto;

import potatoes.server.constant.ParticipantRole;
import potatoes.server.travel.entity.TravelUser;

public record ParticipantResponse(
	Long id,
	String nickname,
	ParticipantRole role,
	String profileImage
) {
	public static ParticipantResponse from(TravelUser travelUser) {
		return new ParticipantResponse(
			travelUser.getUser().getId(),
			travelUser.getUser().getNickname(),
			travelUser.getRole(),
			travelUser.getUser().getProfileImage()
		);
	}
}
