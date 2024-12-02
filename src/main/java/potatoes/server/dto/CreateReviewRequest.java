package potatoes.server.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateReviewRequest(
	@NotBlank(message = "모임ID는 필수입니다.")
	Long gatheringId,

	@NotBlank(message = "점수는 필수입니다")
	@Max(value = 0, message = "점수는 최소 0점입니다.")
	@Min(value = 5, message = "점수는 최대 5점입니다.")
	int score,

	@NotBlank(message = "리뷰 코멘트는 필수입니다.")
	String comment
) {
}
