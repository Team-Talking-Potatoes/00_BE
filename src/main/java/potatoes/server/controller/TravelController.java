package potatoes.server.controller;

import static org.springframework.http.MediaType.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import potatoes.server.dto.CreateTravelRequest;
import potatoes.server.service.TravelService;
import potatoes.server.utils.annotation.Authorization;

@RequiredArgsConstructor
@RequestMapping("/travel")
@RestController
public class TravelController {

	private final TravelService travelService;

	@Operation(summary = "여행 등록", description = "여행을 등록합니다. 자세한 파라미터는 API명세서를 참고해주세요.")
	@PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createTravel(
		@Authorization @Parameter(hidden = true) Long userId,
		@ModelAttribute @Valid CreateTravelRequest createTravelRequest) {
		travelService.createTravel(userId, createTravelRequest);
		return new ResponseEntity<>(HttpStatus.OK);
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
}
