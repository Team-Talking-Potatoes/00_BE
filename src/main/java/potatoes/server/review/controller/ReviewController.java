package potatoes.server.review.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.review.dto.CreateReviewRequest;
import potatoes.server.review.dto.GetDetailsReview;
import potatoes.server.review.dto.TotalRatingResponse;
import potatoes.server.review.service.ReviewService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.annotation.NonLoginAuthorization;

@Tag(name = "리뷰", description = "리뷰 관련 API")
@RequestMapping("/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
	@PostMapping("")
	public ResponseEntity<CommonResponse<?>> createReview(
		@ModelAttribute @Valid CreateReviewRequest request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.createReview(request, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "리뷰 상세 조회", description = "")
	@GetMapping("/{reviewId}")
	public ResponseEntity<CommonResponse<GetDetailsReview>> getDetailsReview(
		@PathVariable Long reviewId,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getDetailsReview(reviewId, userId)));
	}

	@Operation(summary = "리뷰 평점 종합 조회")
	@GetMapping("/{travelId}/ratings")
	public ResponseEntity<CommonResponse<TotalRatingResponse>> getTotalReviewsRatings(
		@PathVariable Long travelId
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getTotalReviewsRatings(travelId)));
	}

	@Operation(summary = "리뷰 좋아요 등록", description = "")
	@PostMapping("/{reviewId}/likes")
	public ResponseEntity<CommonResponse<?>> addReviewLike(
		@PathVariable Long reviewId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.addReviewLike(reviewId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "리뷰 좋아요 취소", description = "")
	@DeleteMapping("/{reviewId}/likes")
	public ResponseEntity<CommonResponse<?>> removeReviewLike(
		@PathVariable Long reviewId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.removeReviewLike(reviewId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}
}
