package potatoes.server.controller;

import static org.springframework.http.MediaType.*;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import potatoes.server.constant.TravelSortType;
import potatoes.server.constant.TravelStatus;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.dto.SimpleTravelResponse;
import potatoes.server.dto.TravelDetailResponse;
import potatoes.server.dto.GetMyTravelResponse;
import potatoes.server.dto.TravelDetailResponse;
import potatoes.server.dto.TravelListResponse;
import potatoes.server.dto.TravelPageResponse;
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

	@Operation(summary = "여행 상세 조회", description = "여행ID를 통해 해당 여행의 상세 내용을 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<TravelDetailResponse> getTravelDetails(@PathVariable(name = "id") Long travelId) {
		return ResponseEntity.ok().body(travelService.getDetails(travelId));
	}

	@Operation(summary = "여행 리스트 조회", description = "조건에 맞는 여행 리스트 조회합니다.")
	@GetMapping()
	public ResponseEntity<TravelListResponse> getTravelList(
		@RequestParam(required = false, defaultValue = "1") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@RequestParam(required = false) boolean isDomestic,
		@RequestParam(required = false) String startAt,
		@RequestParam(required = false) String endAt,
		@RequestParam(required = false, defaultValue = "recent") TravelSortType sortOrder,
		@RequestParam(required = false) String query
	) {
		return ResponseEntity.ok(
			travelService.getTravelList(page - 1, size, isDomestic, startAt, endAt, sortOrder, query));
	}

	@Operation(summary = "내가 만든 여행", description = "내 프로필에서 사용하는 사용자가 생성한 여행리스트를 조회합니다.")
	@GetMapping("/created")
	public ResponseEntity<TravelPageResponse<GetMyTravelResponse>> getMyTravels(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getMyTravels(page, size, userId));
	}

	@Operation(summary = "예정 여행, 다녀온 여행 조회", description = "파라미터의 travelStatus의 값이 upcoming or past에 따라서 예정 여행, 다녀온 여행이 달라진다")
	@GetMapping("/status")
	public ResponseEntity<TravelPageResponse<GetMyTravelResponse>> getMyTravelsByStatus(
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

	@Operation(summary = "사용자 북마크 여행 조회")
	@GetMapping("/checked")
	public ResponseEntity<TravelPageResponse<GetMyTravelResponse>> getMyTravelsByBookmark(
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "4") int size,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(travelService.getMyTravelsByBookmark(page, size, userId));
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

	@Operation(summary = "이번 주 인기 여행 조회", description = "이번 주 인기가 많은 여행 모임 반환")
	@GetMapping("/popular")
	public ResponseEntity<List<SimpleTravelResponse>> getPopularTravels() {
		// TODO - 조회수 카운트 방법 논의 필요
		return ResponseEntity.ok(travelService.getPopularTravels());
	}
}