package potatoes.server.travel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.service.TravelService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.pagination.dto.PageResponse;

@Tag(name = "여행 북마크", description = "여행 북마크 관련 API")
@RequiredArgsConstructor
@RequestMapping("/travels/bookmark")
@RestController
public class BookmarkController {

	private final TravelService travelService;

	@Operation(summary = "사용자 북마크 여행 조회")
	@GetMapping("")
	public ResponseEntity<CommonResponse<PageResponse<GetMyTravelResponse>>> getMyTravelsByBookmark(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getMyTravelsByBookmark(page, size, userId)));
	}

	@Operation(summary = "북마크 등록", description = "Travel ID를 받고 북마크로 등록합니다.")
	@PostMapping("")
	public ResponseEntity<CommonResponse<?>> addBookMark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.addBookmark(userId, travelId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "북마크 삭제", description = "Travel ID를 받고 등록된 북마크를 삭제합니다.")
	@DeleteMapping("")
	public ResponseEntity<CommonResponse<?>> deleteBookmark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.deleteBookmark(userId, travelId);
		return ResponseEntity.ok(CommonResponse.create());
	}
}
