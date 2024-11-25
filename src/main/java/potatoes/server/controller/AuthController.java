package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateUserRequest;
import potatoes.server.dto.CreateUserResponse;
import potatoes.server.dto.SignOutUserResponse;
import potatoes.server.service.AuthService;

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

	@PostMapping("/signout")
	public ResponseEntity<SignOutUserResponse> signOut() {
		return ResponseEntity.ok().body(new SignOutUserResponse("로그아웃 성공"));
	}
}
