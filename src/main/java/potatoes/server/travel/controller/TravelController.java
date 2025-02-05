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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.dto.SimpleTravelResponse;
import potatoes.server.travel.dto.TravelDetailResponse;
import potatoes.server.travel.service.TravelService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.annotation.NonLoginAuthorization;

@Slf4j
@Tag(name = "여행", description = "여행 관련 API")
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

	@Operation(summary = "여행 상세 조회", description = "여행ID를 통해 해당 여행의 상세 내용을 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse<TravelDetailResponse>> getTravelDetails(
		@PathVariable(name = "id") Long travelId,
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		return ResponseEntity.ok(CommonResponse.from(travelService.getDetails(travelId, userId)));
	}

	//FIXME
	// 현재 예정 여행 다녀온 여행 api가 통합 되어있지만, 이는 다녀온 여행의 조건이 시간만 가지고 체크하는건 아니라고 생각이들어
	// 일단은 같은 api돌려쓰고 기획이 좀더 다져지면 그때 분리하는게 좋을것 같습니다.

	@Operation(summary = "여행 취소(관리자)", description = "여행을 취소합니다. 관리자만 취소할 수 있습니다")
	@DeleteMapping("/{travelId}")
	public ResponseEntity<CommonResponse<?>> deleteTravelByOrganizer(
		@PathVariable Long travelId,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		travelService.deleteTravelByOrganizer(travelId, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "이번 주 인기 여행 조회", description = "이번 주 인기가 많은 여행 모임 반환")
	@GetMapping("/popular")
	public ResponseEntity<CommonResponse<List<SimpleTravelResponse>>> getPopularTravels(
		@NonLoginAuthorization @Parameter(hidden = true) Optional<Long> userId
	) {
		// TODO - 조회수 카운트 방법 논의 필요
		return ResponseEntity.ok(CommonResponse.from(travelService.getPopularTravels(userId)));
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
