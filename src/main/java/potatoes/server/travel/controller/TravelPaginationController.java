package potatoes.server.travel.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.dto.SimpleTravelResponse;
import potatoes.server.travel.dto.TravelSummaryResponse;
import potatoes.server.travel.service.TravelService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.annotation.NonLoginAuthorization;
import potatoes.server.utils.constant.TravelSortType;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.pagination.dto.PageResponse;

@Tag(name = "여행 페이지 네이션", description = "여행 페이지 네이션 관련 API")
@RequiredArgsConstructor
@RequestMapping("/travels")
@RestController
public class TravelPaginationController {
	private final TravelService travelService;

	@Operation(summary = "여행 리스트 조회", description = "조건에 맞는 여행 리스트 조회합니다.")
	@GetMapping("")
	public ResponseEntity<CommonResponse<PageResponse<TravelSummaryResponse>>> getTravelList(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@RequestParam(required = false) Boolean isDomestic,
		@RequestParam(required = false) String startAt,
		@RequestParam(required = false) String endAt,
		@RequestParam(required = false, defaultValue = "recent") TravelSortType sortOrder,
		@RequestParam(required = false) String query,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		return ResponseEntity.ok(
			CommonResponse.from(
				travelService.getTravelList(page, size, isDomestic, startAt, endAt, sortOrder, query, userId)));
	}

	@Operation(summary = "이번 주 인기 여행 조회", description = "이번 주 인기가 많은 여행 모임 반환")
	@GetMapping("/popular")
	public ResponseEntity<CommonResponse<PageResponse<SimpleTravelResponse>>> getPopularTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "8") int size,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		// TODO - 조회수 카운트 방법 논의 필요
		return ResponseEntity.ok(CommonResponse.from(travelService.getPopularTravels(page, size, userId)));
	}

	@Operation(summary = "내가 만든 여행", description = "내 프로필에서 사용하는 사용자가 생성한 여행리스트를 조회합니다.")
	@GetMapping("/created")
	public ResponseEntity<CommonResponse<PageResponse<GetMyTravelResponse>>> getMyTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getMyTravels(page, size, userId)));
	}

	@Operation(summary = "예정 여행, 다녀온 여행 조회", description = "파라미터의 travelStatus의 값이 upcoming or past에 따라서 예정 여행, 다녀온 여행이 달라진다")
	@GetMapping("/status")
	public ResponseEntity<CommonResponse<PageResponse<GetMyTravelResponse>>> getMyTravelsByStatus(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@RequestParam TravelStatus travelStatus,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(
			CommonResponse.from(travelService.getTravelsByStatus(page, size, userId, travelStatus))
		);
	}

	@Operation(summary = "리뷰작성이 가능한 여행조회", description = "")
	@GetMapping("/reviews/available")
	public ResponseEntity<CommonResponse<PageResponse<GetMyTravelResponse>>> getReviewableMyTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getReviewableMyTravels(page, size, userId)));
	}
}
