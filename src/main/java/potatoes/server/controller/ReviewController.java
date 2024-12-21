package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateReviewRequest;
import potatoes.server.dto.ReviewPageResponse;
import potatoes.server.service.ReviewService;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "Review", description = "Review API")
@RequestMapping("/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
	@PostMapping("")
	public ResponseEntity<Void> createReview(
		@ModelAttribute @Valid CreateReviewRequest request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.createReview(request, userId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "내가 작성한 리뷰 조회", description = "내가 작성한 리뷰를 조회합니다.")
	@GetMapping("/published")
	public ResponseEntity<ReviewPageResponse> getMyReview(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(reviewService.getMyReviews(page, size, userId));
	}

	@Operation(summary = "리뷰 좋아요 등록", description = "")
	@PostMapping("/{reviewId}/likes")
	public ResponseEntity<Void> addReviewLike(
		@PathVariable Long reviewId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.addReviewLike(reviewId, userId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "리뷰 좋아요 취소", description = "")
	@DeleteMapping("/{reviewId}/likes")
	public ResponseEntity<Void> removeReviewLike(
		@PathVariable Long reviewId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		reviewService.removeReviewLike(reviewId, userId);
		return ResponseEntity.ok().build();
	}
}
