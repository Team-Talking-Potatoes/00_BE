package potatoes.server.controller;

import static org.springframework.http.MediaType.*;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.service.TravelService;
import potatoes.server.utils.annotation.Authorization;

@RequiredArgsConstructor
@RequestMapping("/api/travel")
@RestController
public class TravelController {

	private final TravelService travelService;

	@Operation(summary = "여행 등록", description = "")
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public void createTravel(
		@Authorization @Parameter(hidden = true) Long id,
		@ModelAttribute @Valid CreateTravelRequest createTravelRequest) {
		travelService.createTravel(id, createTravelRequest);
	}
}
