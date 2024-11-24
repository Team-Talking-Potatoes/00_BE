package potatoes.server.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import potatoes.server.constant.GatheringType;

@Builder
public record CreateGatheringRequest(
	@NotNull(message = "위치는 필수입니다")
	String location,

	@NotNull(message = "모임 유형은 필수입니다")
	GatheringType type,

	String name,

	@NotNull(message = "모임 일시는 필수입니다")
	Instant dateTime,

	@NotNull(message = "모임 정원은 필수입니다.")
	int capacity,

	@NotNull(message = "등록 마감일시는 필수입니다")
	String registrationEnd
) {
}
