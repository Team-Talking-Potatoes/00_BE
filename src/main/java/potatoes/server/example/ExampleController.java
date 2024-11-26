package potatoes.server.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Tag(name = "예제용 보일러플레이트")
@RequiredArgsConstructor
@RestController
public class ExampleController {

	private final JwtTokenUtil jwtTokenUtil;

	@Operation(summary = "토큰 발급")
	@GetMapping("/token")
	public String exampleToken(@RequestParam(name = "id") String id) {
		return jwtTokenUtil.createToken(id);
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
