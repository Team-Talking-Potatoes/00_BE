package potatoes.server.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CommonResponse;
import potatoes.server.dto.DeleteUserRequest;
import potatoes.server.dto.GetUserProfileResponse;
import potatoes.server.dto.PasswordCertification;
import potatoes.server.dto.PopularUserResponse;
import potatoes.server.dto.ResetPasswordRequest;
import potatoes.server.user.service.UserService;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "User", description = "User API")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원정보 조회", description = "이메일, 닉네임, 프로필이미지, 자기소개 조회")
	@GetMapping("")
	public ResponseEntity<CommonResponse<GetUserProfileResponse>> getUserProfile(
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(userService.getUserProfile(userId)));
	}

	@Operation(summary = "회원정보 수정", description = "프로필이미지, 닉네임, 설명에 대한 정보 수정")
	@PutMapping("")
	public ResponseEntity<CommonResponse<?>> updateUserProfile(
		@RequestParam(required = false) MultipartFile profileImage,
		@RequestParam(required = false) String nickname,
		@RequestParam(required = false) String description,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		userService.updateUserProfile(profileImage, nickname, description, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "회원탈퇴", description = "토큰과 패스워드를 받는다")
	@DeleteMapping("")
	public ResponseEntity<CommonResponse<?>> deleteUser(
		@RequestBody @Valid PasswordCertification request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		userService.deleteUser(request, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "회원 비밀번호 검증", description = "현재 사용자의 패스워드가 맞는지 검증한다")
	@PostMapping("/password/certification")
	public ResponseEntity<CommonResponse<PasswordCertification>> passwordCertification(
		@RequestBody @Valid DeleteUserRequest request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		return ResponseEntity.ok(CommonResponse.from(userService.certificateAndReturnToken(request, userId)));
	}

	@Operation(summary = "비밀번호 재설정", description = "로그인 된 상황에 대한 비밀번호 재설정")
	@PutMapping("/password")
	public ResponseEntity<CommonResponse<?>> resetPassword(
		@RequestBody @Valid ResetPasswordRequest request,
		@Authorization @Parameter(hidden = true) Long userId
	) {
		userService.resetPassword(request, userId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "이번 달 여행지기 조회", description = "이번 달 만든 모임의 리뷰가 많은 유저 조회")
	@GetMapping("/popular")
	public ResponseEntity<CommonResponse<List<PopularUserResponse>>> getPopularUsers() {
		// TODO - 데이터베이스 전체를 훑는 무거운 작업, 추후 기획 재정리 혹은 Batch job으로 분리 필요
		return ResponseEntity.ok(CommonResponse.from(userService.findPopularUsers()));
	}
}
