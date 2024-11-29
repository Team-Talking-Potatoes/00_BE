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
		return new GetGatheringParticipantResponse(
			entity.getUser().getId(),
			entity.getGathering().getId(),
			entity.getJoinedAt(),
			UserInfo.from(entity.getUser())
		);
	}
}
