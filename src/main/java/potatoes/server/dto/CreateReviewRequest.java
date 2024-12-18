package potatoes.server.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
	@NotNull(message = "여행id는 필수입니다.")
	Long travelId,

	@NotBlank(message = "리뷰 제목은 필수입니다.")
	@Size(min = 1, max = 20, message = "리뷰 제목의 길이는 1자 이상 20자 이하여야 합니다.")
	String title,
	@NotBlank(message = "리뷰 내용은 필수입니다.")
	@Size(min = 1, max = 100, message = "리뷰 내용의 길이는 1자 이상 100자 이하여야 합니다.")
	String comment,
	@Min(value = 0, message = "별점은 0점 이상이어야 합니다.")
	@Max(value = 5, message = "별점은 5점 이하여야 합니다.")
	float starRating,
	List<MultipartFile> images
) {
}
