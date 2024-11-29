package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Gathering;

@Builder
public record GetGatheringResponse(
	Long id,
	GatheringType type,
	String name,
	Instant datetime,
	Instant registrationEnd,
	String location,
	int participantCount,
	int capacity,
	String image,
	Long createdBy,
	Instant canceledAt
) {
	public static GetGatheringResponse from(Gathering gathering) {
		return new GetGatheringResponse(
			gathering.getId(),
			gathering.getType(),
			gathering.getName(),
			gathering.getDateTime(),
			gathering.getRegistrationEnd(),
			gathering.getLocation(),
			gathering.getParticipantCount(),
			gathering.getCapacity(),
			gathering.getImage(),
			gathering.getCreatedBy(),
			gathering.getCanceledAt()
		);
	}
}
