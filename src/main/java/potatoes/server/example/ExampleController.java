package potatoes.server.example;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.infra.s3.S3UtilsProvider;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Tag(name = "예제용 보일러플레이트")
@RequiredArgsConstructor
@RestController
public class ExampleController {

	private final JwtTokenUtil jwtTokenUtil;
	private final S3UtilsProvider s3UtilsProvider;

	@Operation(summary = "s3 멀티파트 등록 예제 테스트")
	@PostMapping(
		value = "/test/file",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<String> postToS3(
		@RequestPart("multipartFile") MultipartFile multipartFile
	) {
		return ResponseEntity.ok(s3UtilsProvider.uploadFile(multipartFile));
	}

	@Operation(summary = "s3 멀티파트 다중 등록 예제 테스트")
	@PostMapping(
		value = "/test/files",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<List<String>> postToS3(
		@RequestPart("multipartFiles") List<MultipartFile> multipartFiles
	) {
		return ResponseEntity.ok(s3UtilsProvider.uploadFiles(multipartFiles));
	}

	@Operation(summary = "s3 파일이름으로 주소 찾기")
	@PostMapping("/test/file/url")
	public ResponseEntity<String> generateUrl(
		@RequestParam String filename
	) {
		return ResponseEntity.ok(s3UtilsProvider.getFileUrl(filename));
	}

	@Operation(summary = "토큰 발급")
	@GetMapping("/token")
	public String exampleToken(@RequestParam(name = "id") String id) {
		return jwtTokenUtil.createAccessToken(id);
	}

	@Operation(summary = "토큰 해독", description = "Authorize에 토큰을 넣으세요")
	@GetMapping("/token/decode")
	public Long exampleToken2(@Authorization @Parameter(hidden = true) Long id) {
		return id;
	}

	@Operation(
		summary = "에러 발생",
		description = """ 
			1. 이렇게 문서식으로 쓸수도 있어요
			2. 현재 api는 400에러 발생입니다.
			3. 스웨거 문서가 보기어렵거나, 요구사항있으면 편하게 말해주세요.
			"""
	)
	@GetMapping("/error/400")
	public ResponseEntity<Void> create400() {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
	}

	@Operation(summary = "401 에러")
	@GetMapping("/error/401")
	public ResponseEntity<Void> create401() {
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
	}

	@Operation(summary = "403 에러")
	@GetMapping("/error/403")
	public ResponseEntity<Void> create403() {
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
	}

	@Operation(summary = "500 에러")
	@GetMapping("/error/500")
	public ResponseEntity<Void> create500() {
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
	}

	// FIXME 토큰 사용예시용으로 등록한 테스트 컨트롤러이며 추후에 삭제 요망
}
