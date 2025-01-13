package potatoes.server.travel.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;
import java.util.Optional;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.dto.GetMyTravelResponse;
import potatoes.server.travel.dto.SimpleTravelResponse;
import potatoes.server.travel.dto.TravelDetailResponse;
import potatoes.server.travel.dto.TravelSummaryResponse;
import potatoes.server.travel.service.TravelService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.annotation.NonLoginAuthorization;
import potatoes.server.utils.constant.TravelSortType;
import potatoes.server.utils.constant.TravelStatus;
import potatoes.server.utils.pagination.dto.PageResponse;

@RequiredArgsConstructor
@RequestMapping("/travels")
@RestController
public class TravelController {

	private final TravelService travelService;

	@Operation(summary = "여행 등록", description = "여행을 등록합니다. 자세한 파라미터는 API명세서를 참고해주세요.")
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<?>> createTravel(
		@Authorization @Parameter(hidden = true) Long userId,
		@ModelAttribute @Valid CreateTravelRequest createTravelRequest
	) {
		travelService.createTravel(userId, createTravelRequest);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "여행 리스트 조회", description = "조건에 맞는 여행 리스트 조회합니다.")
	@GetMapping()
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


	@Operation(summary = "여행 상세 조회", description = "여행ID를 통해 해당 여행의 상세 내용을 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse<TravelDetailResponse>> getTravelDetails(
		@PathVariable(name = "id") Long travelId,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getDetails(travelId, userId)));
	}

	@Operation(summary = "이번 주 인기 여행 조회", description = "이번 주 인기가 많은 여행 모임 반환")
	@GetMapping("/popular")
	public ResponseEntity<CommonResponse<List<SimpleTravelResponse>>> getPopularTravels(
		@RequestParam(required = false, defaultValue = "1") int page,
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
			CommonResponse.from(travelService.getTravelsByStatus(page, size, userId, travelStatus)));
	}

	//FIXME
	// 현재 예정 여행 다녀온 여행 api가 통합 되어있지만, 이는 다녀온 여행의 조건이 시간만 가지고 체크하는건 아니라고 생각이들어
	// 일단은 같은 api돌려쓰고 기획이 좀더 다져지면 그때 분리하는게 좋을것 같습니다.

	@Operation(summary = "사용자 북마크 여행 조회")
	@GetMapping("/checked")
	public ResponseEntity<CommonResponse<PageResponse<GetMyTravelResponse>>> getMyTravelsByBookmark(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getMyTravelsByBookmark(page, size, userId)));
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
	public ResponseEntity<CommonResponse<?>> addBookMark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.addBookmark(userId, travelId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "북마크 삭제", description = "Travel ID를 받고 등록된 북마크를 삭제합니다.")
	@DeleteMapping("/bookmark")
	public ResponseEntity<CommonResponse<?>> deleteBookmark(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(name = "travelId") Long travelId
	) {
		travelService.deleteBookmark(userId, travelId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "여행 취소(관리자)", description = "여행을 취소합니다. 관리자만 취소할 수 있습니다")
	@DeleteMapping("/{travelId}")
	public ResponseEntity<CommonResponse<?>> deleteTravelByOrganizer(
		@PathVariable Long travelId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		travelService.deleteTravelByOrganizer(travelId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "동행 (참여자)", description = "여행을 동행합니다.")
	@PostMapping("{travelId}/participation")
	public ResponseEntity<CommonResponse<?>> participateInTravel(
		@PathVariable Long travelId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		travelService.participateInTravel(travelId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "동행 취소(참여자)", description = "여행 동행을 취소합니다. 이미 참여중인 여행에만 취소할 수 있습니다")
	@DeleteMapping("{travelId}/participation")
	public ResponseEntity<CommonResponse<?>> deleteTravelByAttendee(
		@PathVariable Long travelId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		travelService.deleteTravelByAttendee(travelId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}
}
