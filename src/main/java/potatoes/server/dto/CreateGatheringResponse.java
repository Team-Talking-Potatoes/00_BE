package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.constant.GatheringType;

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
}
