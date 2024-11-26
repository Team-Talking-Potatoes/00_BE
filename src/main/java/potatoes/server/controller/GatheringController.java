package potatoes.server.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import potatoes.server.constant.GatheringType;
import potatoes.server.constant.SortByType;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.dto.GetDetailedGatheringResponse;
import potatoes.server.dto.GetGatheringRequest;
import potatoes.server.dto.GetGatheringResponse;
import potatoes.server.service.GatheringService;
import potatoes.server.utils.Pagination.PageableFactory;
import potatoes.server.utils.annotation.Authorization;

@RestController
@RequestMapping("/gatherings")
@RequiredArgsConstructor
public class GatheringController {

	private final GatheringService gatheringService;
	private final PageableFactory pageableFactory;

	private static final String DEFAULT_SORT_BY = "createdAt";
	private static final String DEFAULT_SORT_ORDER = "desc";

	@GetMapping("/")
	public List<GetGatheringResponse> getGatherings(@RequestParam(required = false) String id,
		@RequestParam(required = false) GatheringType type, @RequestParam(required = false) String location,
		@RequestParam(required = false) String date, @RequestParam(required = false) Long createdBy,
		@RequestParam(required = false, defaultValue = DEFAULT_SORT_BY) SortByType sortBy,
		@RequestParam(required = false, defaultValue = DEFAULT_SORT_ORDER) String sortOrder,
		@RequestParam(required = false, defaultValue = "20") @Valid @Min(value = 1, message = "Limit의 최소값은 1입니다.") int limit,
		@RequestParam(required = false, defaultValue = "0") @Valid @Min(value = 0, message = "offset의 최소값은 0입니다.") int offset) {
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
		@Authorization Long memberId,
		@RequestPart("gatheringInfo") @Valid CreateGatheringRequest request,
		@RequestPart(value = "image", required = false) MultipartFile multipartFile) {
		return gatheringService.integrateGatheringCreation(request, multipartFile, memberId);
	}

	@GetMapping("/{id}")
	public GetDetailedGatheringResponse getDetailedGathering(
		@Authorization Long memberId,
		@PathVariable Long id
	) {
		return gatheringService.getDetailedGathering(id);
	}

}
