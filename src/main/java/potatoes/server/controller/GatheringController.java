package potatoes.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
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

	@Operation(summary = "모임 목록 조회", description = "Authorize에 토큰을 넣으세요")
	@GetMapping("/")
	public List<GetGatheringResponse> getGatherings(
		@RequestParam(required = false) String id,
		@RequestParam(required = false) GatheringType type,
		@RequestParam(required = false) String location,
		@RequestParam(required = false) String date,
		@RequestParam(required = false) Long createdBy,
		@RequestParam(required = false) String sortBy,
		@RequestParam(required = false) String sortOrder,
		@RequestParam(required = false) @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") Integer limit,
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

	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CreateGatheringResponse createGathering(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestPart("gatheringInfo") @Valid CreateGatheringRequest request,
		@RequestPart(value = "image", required = false) MultipartFile multipartFile
	) {
		return gatheringService.integrateGatheringCreation(request, multipartFile, userId);
	}

	@GetMapping("/{gatheringId}")
	public GetDetailedGatheringResponse getDetailedGathering(
		@Authorization @Parameter(hidden = true) Long userId,
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
