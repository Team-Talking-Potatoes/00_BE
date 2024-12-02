package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateUserRequest;
import potatoes.server.dto.CreateUserResponse;
import potatoes.server.dto.GetUserResponse;
import potatoes.server.dto.SignInUserRequest;
import potatoes.server.dto.SignInUserResponse;
import potatoes.server.dto.SignOutUserResponse;
import potatoes.server.dto.UpdateUserRequest;
import potatoes.server.service.AuthService;
import potatoes.server.utils.annotation.Authorization;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "회원 가입", description = "회원가입")
	@PostMapping("/signup")
	public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
		authService.register(request);
		return ResponseEntity.ok().body(new CreateUserResponse("사용자 생성 성공"));
	}

	@Operation(summary = "로그인", description = "아이디와 비밀번호를 받아 JWT를 request body에 넣어 응답해줍니다.")
	@PostMapping("/signin")
	public ResponseEntity<SignInUserResponse> signIn(@RequestBody @Valid SignInUserRequest request) {
		return ResponseEntity.ok().body(new SignInUserResponse(authService.signIn(request)));
	}

	@Operation(summary = "로그아웃", description = "제공된 API명세서 표시가 되있어 일단 추가된 엔드포인트 입니다.")
	@PostMapping("/signout")
	public ResponseEntity<SignOutUserResponse> signOut() {
		return ResponseEntity.ok().body(new SignOutUserResponse("로그아웃 성공"));
	}

	@Operation(summary = "회원 정보 조회", description = "회원 ID를 통해 해당 회원의 정보를 반환해줍니다.")
	@GetMapping("/user")
	public ResponseEntity<GetUserResponse> getUserInfo(@Authorization @Parameter(hidden = true) Long userId) {
		return ResponseEntity.ok().body(authService.find(userId));
	}

	@Operation(summary = "회원 정보 수정", description = "회원의 이미지, 회사 이름을 수정합니다.")
	@PutMapping("/user")
	public ResponseEntity<GetUserResponse> updateUserInfo(@Authorization @Parameter(hidden = true) Long userId,
		@RequestBody @Valid UpdateUserRequest request) {
		return ResponseEntity.ok().body(authService.update(userId, request));
	}
}
