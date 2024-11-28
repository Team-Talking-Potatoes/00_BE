package potatoes.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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
	public List<GetGatheringResponse> getGatherings(
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
		@Parameter(description = "한 번에 조회할 모임 수 (최소 1) 미입력시 20")
		@RequestParam(required = false) @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") Integer limit,
		@Parameter(description = "조회 시작 위치 (최소 0) 미입력시 0")
		@RequestParam(required = false) @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") Integer offset
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
		return gatheringService.getGatherings(request, pageable);
	}

	@Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다")
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateGatheringResponse createGathering(
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
		return gatheringService.integrateGatheringCreation(request, multipartFile, userId);
	}

	@Operation(summary = "모임 상세 조회", description = "모임의 상세 정보를 조회합니다")
	@GetMapping("/{gatheringId}")
	public GetDetailedGatheringResponse getDetailedGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@Parameter(description = "모임 ID")
		@PathVariable Long gatheringId
	) {
		return gatheringService.getDetailedGathering(gatheringId);
	}

	@GetMapping("/{gatheringId}/participants")
	public List<GetGatheringParticipantResponse> getDetailedParticipantGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable Long gatheringId,
		@RequestParam(required = false) String sortBy,
		@RequestParam(required = false) String sortOrder,
		@RequestParam(required = false, defaultValue = "5") @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") Integer limit,
		@RequestParam(required = false, defaultValue = "0") @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") Integer offset
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
		return gatheringService.getGatheringParticipant(request, pageable);
	}

	@PutMapping("/{gatheringId}/cancel")
	public PutGatheringResponse putGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable Long gatheringId
	) {
		return gatheringService.putGathering(userId, gatheringId);
	}

	@PostMapping("/{gatheringId}/join")
	public void postGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable Long gatheringId
	) {
		gatheringService.putGathering(userId, gatheringId);
	}
	//TODO DTO 내부로 기본값 최솟값 이런거 넣어주기 현재는 컨트롤러에서 잡고있음
	//TODO 변수명 통일
}
