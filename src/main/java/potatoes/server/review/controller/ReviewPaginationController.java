package potatoes.server.review.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.review.dto.GetMyReviewResponse;
import potatoes.server.review.dto.GetReviewInTravel;
import potatoes.server.review.dto.GetReviewResponse;
import potatoes.server.review.dto.SimpleReviewResponse;
import potatoes.server.review.service.ReviewService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.annotation.NonLoginAuthorization;
import potatoes.server.utils.constant.SortByType;
import potatoes.server.utils.pagination.dto.PageResponse;

@Tag(name = "리뷰 페이지 네이션", description = "리뷰 페이지 네이션 관련 API")
@RequestMapping("/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewPaginationController {
	private final ReviewService reviewService;

	@Operation(summary = "리뷰 목록 조회", description = "최신순 인기순 조건이있습니다. 디폴트값(page = 0, size = 10)")
	@GetMapping("")
	public ResponseEntity<CommonResponse<PageResponse<GetReviewResponse>>> getReviews(
		@RequestParam(defaultValue = "LATEST") SortByType sortByType,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getReviews(sortByType, page, size, userId)));
	}

	@Operation(summary = "리뷰 리스트 조회", description = "최근에 추가된 리뷰를 조회합니다.")
	@GetMapping("/popular")
	public ResponseEntity<CommonResponse<List<SimpleReviewResponse>>> getSimpleReviewList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "6") int size
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getSimpleReviews(page, size)));
	}

	@Operation(summary = "내가 작성한 리뷰 조회", description = "내가 작성한 리뷰를 조회합니다.")
	@GetMapping("/mine")
	public ResponseEntity<CommonResponse<PageResponse<GetMyReviewResponse>>> getMyReview(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getMyReviews(page, size, userId)));
	}

	@Operation(summary = "여행에 달린 리뷰 조회")
	@GetMapping("/travels/{travelId}")
	public ResponseEntity<CommonResponse<PageResponse<GetReviewInTravel>>> getReviewsInTravel(
		@PathVariable Long travelId,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size
	) {
		return ResponseEntity.ok(CommonResponse.from(reviewService.getReviewsInTravel(travelId, page, size)));
	}
}
