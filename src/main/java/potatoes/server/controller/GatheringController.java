package potatoes.server.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateGatheringRequest;
import potatoes.server.dto.CreateGatheringResponse;
import potatoes.server.service.GatheringService;

@RestController
@RequestMapping("/gatherings")
@RequiredArgsConstructor
public class GatheringController {

	private final GatheringService gatheringService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CreateGatheringResponse createGathering(
		@Authorization
		@Parameter(hidden = true)
		memberId: Long,

		@RequestPart("gatheringInfo") @Valid CreateGatheringRequest request,
		@RequestPart(value = "image", required = false) MultipartFile image
	) {

	}
}
