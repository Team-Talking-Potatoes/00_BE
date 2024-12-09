package potatoes.server.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SignInRequest;
import potatoes.server.dto.SignUpRequest;
import potatoes.server.service.UserService;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@Operation(summary = "로그인", description = "로그인을 성공하면 SET_COOKIE형태로 토큰이 설정됩니다.")
	@PostMapping("/sign-in")
	public ResponseEntity<Void> signIn(@RequestBody @Valid SignInRequest request) {
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, userService.signIn(request).toString())
			.build();
	}

	@Operation(summary = "회원가입", description = "")
	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
		userService.signUp(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "토큰 유효성 검사", description = "토큰의 유효성을 검사합니다. 조작된 토큰이면 예외를 터트립니다. 헤더에 토큰을 담아주세요")
	@GetMapping("/token/verify")
	public ResponseEntity<Void> validateToken(@Authorization @Parameter(hidden = true) Long token) {
		return ResponseEntity.ok().build();
	}
}
