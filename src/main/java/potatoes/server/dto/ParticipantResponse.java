package potatoes.server.dto;

import potatoes.server.travel.entity.TravelUser;
import potatoes.server.utils.constant.ParticipantRole;

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
