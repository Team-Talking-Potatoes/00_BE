package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.ResetPasswordRequest;
import potatoes.server.service.UserService;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "User", description = "User API")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@Operation(summary = "비밀번호 재설정", description = "로그인 된 상황에 대한 비밀번호 재설정")
	@PutMapping("/password")
	public ResponseEntity<Void> resetPassword(
		@RequestBody @Valid ResetPasswordRequest request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		userService.resetPassword(request, userId);
		return ResponseEntity.ok().build();
	}
}
