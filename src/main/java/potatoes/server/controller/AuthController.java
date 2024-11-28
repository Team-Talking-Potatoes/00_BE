package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/signup")
	public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
		authService.register(request);
		return ResponseEntity.ok().body(new CreateUserResponse("사용자 생성 성공"));
	}

	@PostMapping("/signin")
	public ResponseEntity<SignInUserResponse> signIn(@RequestBody @Valid SignInUserRequest request) {
		return ResponseEntity.ok().body(new SignInUserResponse(authService.signIn(request)));
	}

	@PostMapping("/signout")
	public ResponseEntity<SignOutUserResponse> signOut() {
		return ResponseEntity.ok().body(new SignOutUserResponse("로그아웃 성공"));
	}

	@GetMapping("/user")
	public ResponseEntity<GetUserResponse> getUserInfo(@Authorization Long id) {
		return ResponseEntity.ok().body(authService.find(id));
	}

	@PutMapping("/user")
	public ResponseEntity<GetUserResponse> updateUserInfo(@Authorization Long id,
		@RequestBody @Valid UpdateUserRequest request) {
		return ResponseEntity.ok().body(authService.update(id, request));
	}
}
