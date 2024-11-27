package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.entity.Gathering;

@Builder
public record PutGatheringResponse(
	Long id,
	String type,
	String name,
	Instant dateTime,
	Instant registrationEnd,
	String location,
	int participantCount,
	int capacity,
	String image,
	Long createdBy,
	Instant canceledAt
) {
	public static PutGatheringResponse from(Gathering entity) {
		return PutGatheringResponse.builder()
			.id(entity.getId())
			.type(entity.getType().name())
			.name(entity.getName())
			.dateTime(entity.getDateTime())
			.registrationEnd(entity.getRegistrationEnd())
			.location(entity.getLocation())
			.participantCount(entity.getParticipantCount())
			.capacity(entity.getCapacity())
			.image(entity.getImage())
			.createdBy(entity.getCreatedBy())
			.canceledAt(entity.getCanceledAt())
			.build();
	}
}
