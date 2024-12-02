package potatoes.server.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.dto.CreateReviewRequest;
import potatoes.server.dto.CreateReviewResponse;
import potatoes.server.dto.GetReviewRequest;
import potatoes.server.dto.GetReviewResponse;
import potatoes.server.dto.GetReviewScoreInfo;
import potatoes.server.service.ReviewService;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.pagination.PageableFactory;

@RestController
@RequestMapping("/reviws")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final PageableFactory pageableFactory;

	@PostMapping("/")
	public ResponseEntity<CreateReviewResponse> createReview(@Authorization Long userId,
		@RequestBody CreateReviewRequest request) {
		return ResponseEntity.ok().body(reviewService.createReview(userId, request));
	}

	@GetMapping("/")
	public ResponseEntity<List<GetReviewResponse>> getReviews(
		@Parameter(description = "모임 ID로 필터링")
		@RequestParam(required = false) Long gatheringId,
		@Parameter(description = "사용자 ID로 필터링")
		@RequestParam(required = false) Long userId,
		@Parameter(description = "모임 종류로 필터링")
		@RequestParam(required = false) GatheringType type,
		@Parameter(description = "모임 위치로 필터링")
		@RequestParam(required = false) LocationType location,
		@Parameter(description = "모임 날짜로 필터링 (YYYY-MM-DD 형식)")
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
		@Parameter(description = "모임 마감 날짜로 필터링 (YYYY-MM-DD 형식)")
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String dueDate,
		@Parameter(description = "정렬 기준 미입력시 dateTime")
		@RequestParam(required = false) String sortBy,
		@Parameter(description = "정렬 순서 (asc 또는 desc) 미입력시 asc")
		@RequestParam(required = false) String sortOrder,
		@Parameter(description = "한 번에 조회할 모임 수 (최소 1)")
		@RequestParam(required = false, defaultValue = "20") @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,
		@Parameter(description = "조회 시작 위치 (최소 0)")
		@RequestParam(required = false, defaultValue = "0") @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") int offset
	) {
		GetReviewRequest request = GetReviewRequest.builder()
			.gatheringId(gatheringId)
			.userId(userId)
			.type(type)
			.location(location)
			.date(date)
			.dueDate(dueDate)
			.limit(limit)
			.offset(offset)
			.sortBy(sortBy)
			.sortOrder(sortOrder)
			.build();
		Pageable pageable = pageableFactory.create(request);
		return ResponseEntity.ok(reviewService.getReviews(request, pageable));
	}

	@GetMapping("/scores")
	public ResponseEntity<List<GetReviewScoreInfo>> getReviewScores(
		@Parameter(description = "쉼표로 구분된 모임 ID 목록으로 필터링")
		@RequestParam(required = false) String id
	) {
		return ResponseEntity.ok(reviewService.getReviewScoreAverage(id));
	}
}
