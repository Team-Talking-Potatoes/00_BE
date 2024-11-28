package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.entity.UserGathering;

@Builder
public record GetGatheringParticipantResponse(
	Long userId,
	Long gatheringId,
	Instant joinedAt,
	UserInfo user
) {
	public static GetGatheringParticipantResponse from(UserGathering entity) {
		return GetGatheringParticipantResponse.builder()
			.userId(entity.getUser().getId())
			.gatheringId(entity.getGathering().getId())
			.joinedAt(entity.getJoinedAt())
			.user(UserInfo.from(entity.getUser()))
			.build();
	}
}
