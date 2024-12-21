package potatoes.server.controller;

import static org.springframework.http.MediaType.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.constant.TravelStatus;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.dto.PageResponse;
import potatoes.server.service.TravelService;
import potatoes.server.utils.annotation.Authorization;

@RequiredArgsConstructor
@RequestMapping("/travels")
@RestController
public class TravelController {

	private final TravelService travelService;

	@Operation(summary = "여행 등록", description = "여행을 등록합니다. 자세한 파라미터는 API명세서를 참고해주세요.")
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createTravel(
		@Authorization @Parameter(hidden = true) Long userId,
		@ModelAttribute @Valid CreateTravelRequest createTravelRequest
	) {
		travelService.createTravel(userId, createTravelRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "내가 만든 여행", description = "내 프로필에서 사용하는 사용자가 생성한 여행리스트를 조회합니다.")
	@GetMapping("/created")
	public ResponseEntity<PageResponse<GetMyTravelResponse>> getMyTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getMyTravels(page, size, userId));
	}

	@Operation(summary = "예정 여행, 다녀온 여행 조회", description = "파라미터의 travelStatus의 값이 upcoming or past에 따라서 예정 여행, 다녀온 여행이 달라진다")
	@GetMapping("/status")
	public ResponseEntity<PageResponse<GetMyTravelResponse>> getMyTravelsByStatus(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@RequestParam TravelStatus travelStatus,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getTravelsByStatus(page, size, userId, travelStatus));
	}

	//FIXME
	// 현재 예정 여행 다녀온 여행 api가 통합 되어있지만, 이는 다녀온 여행의 조건이 시간만 가지고 체크하는건 아니라고 생각이들어
	// 일단은 같은 api돌려쓰고 기획이 좀더 다져지면 그때 분리하는게 좋을것 같습니다.

	@Operation(summary = "사용자 북마크 여행 조회", description = "")
	@GetMapping("/checked")
	public ResponseEntity<PageResponse<GetMyTravelResponse>> getMyTravelsByBookmark(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getMyTravelsByBookmark(page, size, userId));
	}

	@Operation(summary = "리뷰작성이 가능한 여행조회", description = "")
	@GetMapping("/reviews/pending")
	public ResponseEntity<PageResponse<GetMyTravelResponse>> getReviewableMyTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getReviewableMyTravels(page, size, userId));
	}

	@Operation(summary = "북마크 등록", description = "Travel ID를 받고 북마크로 등록합니다.")
	@PostMapping("/bookmark")
	public ResponseEntity<Void> addBookMark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.addBookmark(userId, travelId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "북마크 삭제", description = "Travel ID를 받고 등록된 북마크를 삭제합니다.")
	@DeleteMapping("/bookmark")
	public ResponseEntity<Void> deleteBookmark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.deleteBookmark(userId, travelId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
