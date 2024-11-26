package potatoes.server.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import potatoes.server.constant.GatheringType;

@Builder
public record GetJoinedGatheringResponse(
	Long id,
	GatheringType type,
	String name,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	Instant dateTime,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	Instant registrationEnd,
	String location,
	int participantCount,
	int capacity,
	String image,
	Long createdBy,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	Instant canceledAt,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	Instant joinedAt,
	boolean isCompleted,
	boolean isReviewed
) {
	// @Builder
	// public static GetGatheringResponse from(Gathering gathering) {
	// 	return GetGatheringResponse.builder()
	// 		.id(gathering.getId())
	// 		.type(gathering.getType())
	// 		.name(gathering.getName())
	// 		.datetime(gathering.getDateTime())
	// 		.registrationEnd(gathering.getRegistrationEnd())
	// 		.location(gathering.getLocation())
	// 		.participantCount(gathering.getParticipantCount())
	// 		.capacity(gathering.getCapacity())
	// 		.image(gathering.getImage())
	// 		.createdBy(gathering.getCreatedBy())
	// 		.canceledAt(gathering.getCanceledAt())
	// 		.joinedAt(participant != null ? participant.getJoinedAt() : null)
	// 		.isCompleted(isCompleted(gathering.getDateTime()))
	// 		.isReviewed(participant != null && participant.isReviewed())
	// 		.build();
	// }
	//
	// private static boolean isCompleted(Instant dateTime) {
	// 	return dateTime != null && dateTime.isBefore(Instant.now());
	// }
}


