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
		return GetGatheringResponse.builder()
			.id(gathering.getId())
			.type(gathering.getType())
			.name(gathering.getName())
			.location(gathering.getLocation())
			.datetime(gathering.getDateTime())
			.registrationEnd(gathering.getRegistrationEnd())
			.participantCount(gathering.getParticipantCount())
			.capacity(gathering.getCapacity())
			.image(gathering.getImage())
			.createdBy(gathering.getCreatedBy())
			.build();
	}
}
