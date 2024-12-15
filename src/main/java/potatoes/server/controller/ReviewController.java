package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateReviewRequest;
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
}
