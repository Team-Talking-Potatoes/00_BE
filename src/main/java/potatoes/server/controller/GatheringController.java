package potatoes.server.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
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

	// @GetMapping("/")
	// public Page<GetGatheringResponse> getGathering(
	// 	@RequestBody GetGatheringRequest req
	// ) {
	// 	return gatheringService.getGatheringResponseList(req);
	// }

	@GetMapping("/")
	public List<GetGatheringResponse> getGatherings(
		@RequestBody GetGatheringRequest req
	) {
		Pageable pageable = pageableFactory.create(req);
		return gatheringService.getGatherings(req, pageable);
	}
	
	@PostMapping(
		value = "/",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public CreateGatheringResponse createGathering(
		@Authorization Long memberId,
		@RequestPart("gatheringInfo") @Valid CreateGatheringRequest request,
		@RequestPart(value = "image", required = false) MultipartFile multipartFile) {
		return gatheringService.integrateGatheringCreation(request, multipartFile, memberId);
	}

}
