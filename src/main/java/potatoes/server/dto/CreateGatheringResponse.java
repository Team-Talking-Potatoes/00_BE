package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.constant.GatheringType;
import potatoes.server.entity.Gathering;

@Builder
public record CreateGatheringResponse(
	Long id,
	GatheringType type,
	String name,
	Instant dateTime,
	Instant registrationEnd,
	String location,
	int participantCount,
	int capacity,
	String image,
	Long createdBy
) {
	public static CreateGatheringResponse from(Gathering gathering) {
		return CreateGatheringResponse.builder()
			.id(gathering.getId())
			.type(gathering.getType())
			.name(gathering.getName())
			.dateTime(gathering.getDateTime())
			.registrationEnd(gathering.getRegistrationEnd())
			.location(gathering.getLocation())
			.participantCount(gathering.getParticipantCount())
			.capacity(gathering.getCapacity())
			.image(gathering.getImage())
			.createdBy(gathering.getCreatedBy())
			.build();
	}
}
