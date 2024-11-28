package potatoes.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.LocationType;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetDetailedGatheringResponse;
import potatoes.server.dto.GetGatheringParticipantRequest;
import potatoes.server.dto.GetGatheringParticipantResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
import potatoes.server.dto.PutGatheringResponse;
import potatoes.server.dto.SuccessResponse;
import potatoes.server.service.GatheringService;
import potatoes.server.utils.Pagination.PageableFactory;
import potatoes.server.utils.annotation.Authorization;

@RestController
@RequestMapping("/gatherings")
@RequiredArgsConstructor
public class GatheringController {

	private final GatheringService gatheringService;
	private final PageableFactory pageableFactory;

	@Operation(summary = "모임 목록 조회", description = "모임의 종류, 위치, 날짜 등 다양한 조건으로 모임 목록을 조회합니다")
	@GetMapping("/")
	public ResponseEntity<List<GetGatheringResponse>> getGatherings(
		@Parameter(description = "쉼표로 구분된 모임 ID 목록으로 필터링")
		@RequestParam(required = false) String id,
		@Parameter(description = "모임 종류로 필터링")
		@RequestParam(required = false) GatheringType type,
		@Parameter(description = "모임 종류로 필터링")
		@RequestParam(required = false) LocationType location,
		@Parameter(description = "모임 날짜로 필터링 (YYYY-MM-DD 형식)")
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
		@Parameter(description = "모임 생성자로 필터링")
		@RequestParam(required = false) Long createdBy,
		@Parameter(description = "정렬 기준 미입력시 dateTime")
		@RequestParam(required = false) String sortBy,
		@Parameter(description = "정렬 순서 (asc 또는 desc) 미입력시 asc")
		@RequestParam(required = false) String sortOrder,
		@Parameter(description = "한 번에 조회할 모임 수 (최소 1)")
		@RequestParam(required = false, defaultValue = "20") @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,
		@Parameter(description = "조회 시작 위치 (최소 0)")
		@RequestParam(required = false, defaultValue = "0") @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") int offset
	) {
		List<Long> idList = id != null ?
			Arrays.stream(id.split(",")).map(String::trim).map(Long::parseLong).collect(Collectors.toList()) : null;

		GetGatheringRequest request = GetGatheringRequest.builder()
			.ids(idList)
			.type(type)
			.location(location)
			.date(date)
			.createdBy(createdBy)
			.sortBy(sortBy)
			.sortOrder(sortOrder)
			.limit(limit)
			.offset(offset)
			.build();

		Pageable pageable = pageableFactory.create(request);
		return ResponseEntity.ok(gatheringService.getGatherings(request, pageable));
	}

	@Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다")
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CreateGatheringResponse> createGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = """ 
			### 1. location - 모임장소
			### 2. type - 모임 서비스 종류
			### 3. name - 모임 이름
			### 4. dateTime - 모임 날짜 및 시간 (YYYY-MM-DDTHH:MM:SS)
			### 5. capacity - 모집 정원 (최소 5인 이상)
			### 6. registrationEnd - 모임 모집 마감 날짜 및 시간 (선택 사항, YYYY-MM-DDTHH:MM:SS)
			""")
		@RequestPart CreateGatheringRequest request,
		@Parameter(description = "이미지")
		@RequestPart(value = "image", required = false) MultipartFile multipartFile
	) {
		return ResponseEntity.ok(gatheringService.integrateGatheringCreation(request, multipartFile, userId));
	}

	@Operation(summary = "모임 상세 조회", description = "모임의 상세 정보를 조회합니다")
	@GetMapping("/{gatheringId}")
	public ResponseEntity<GetDetailedGatheringResponse> getDetailedGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId
	) {
		return ResponseEntity.ok(gatheringService.getDetailedGathering(gatheringId));
	}

	@Operation(summary = "특정 모임의 참가자 목록 조회", description = "특정 모임의 참가자 목록을 페이지네이션하여 조회합니다.")
	@GetMapping("/{gatheringId}/participants")
	public ResponseEntity<List<GetGatheringParticipantResponse>> getDetailedParticipantGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId,
		@Parameter(description = "정렬 기준")
		@RequestParam(required = false) String sortBy,
		@Parameter(description = "정렬 순서")
		@RequestParam(required = false) String sortOrder,
		@Parameter(description = "페이지당 참가자 수")
		@RequestParam(required = false, defaultValue = "5") @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,
		@Parameter(description = "페이지 오프셋")
		@RequestParam(required = false, defaultValue = "0") @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") int offset
	) {
		GetGatheringParticipantRequest request = GetGatheringParticipantRequest.builder()
			.userId(userId)
			.gatheringId(gatheringId)
			.sortBy(sortBy)
			.sortOrder(sortOrder)
			.limit(limit)
			.offset(offset)
			.build();

		Pageable pageable = pageableFactory.create(request);
		return ResponseEntity.ok(gatheringService.getGatheringParticipants(request, pageable));
	}

	@Operation(summary = "모임 취소", description = "모임을 취소합니다. 모임 생성자만 취소할 수 있습니다.")
	@PutMapping("/{gatheringId}/cancel")
	public ResponseEntity<PutGatheringResponse> cancelGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId
	) {
		return ResponseEntity.ok(gatheringService.cancelGathering(userId, gatheringId));
	}

	@Operation(summary = "모임 참여", description = "로그인한 사용자가 모임에 참여합니다")
	@PostMapping("/{gatheringId}/join")
	public ResponseEntity<SuccessResponse> joinGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId
	) {
		return ResponseEntity.ok(gatheringService.joinGathering(userId, gatheringId));
	}

	@Operation(summary = "모임 참여 취소", description = "사용자가 모임에서 참여 취소합니다. 이미 지난 모임은 참여 취소가 불가능합니다.")
	@DeleteMapping("/{gatheringId}/leave")
	public ResponseEntity<SuccessResponse> cancelGatheringParticipation(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId
	) {
		return ResponseEntity.ok(gatheringService.cancelGatheringParticipation(userId, gatheringId));
	}
}
