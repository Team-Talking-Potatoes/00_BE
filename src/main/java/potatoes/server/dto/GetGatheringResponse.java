package potatoes.server.dto;

import java.time.Instant;

import lombok.Builder;
import potatoes.server.constant.GatheringType;

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
	int createdBy,
	Instant canceledAt
) {
}
