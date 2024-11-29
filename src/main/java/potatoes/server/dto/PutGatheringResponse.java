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
		return new PutGatheringResponse(
			entity.getId(),
			entity.getType().name(),
			entity.getName(),
			entity.getDateTime(),
			entity.getRegistrationEnd(),
			entity.getLocation(),
			entity.getParticipantCount(),
			entity.getCapacity(),
			entity.getImage(),
			entity.getCreatedBy(),
			entity.getCanceledAt()
		);
	}
}
