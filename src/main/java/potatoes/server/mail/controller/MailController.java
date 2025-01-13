package potatoes.server.mail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.mail.dto.EmailVerifyRequest;
import potatoes.server.mail.dto.SendMailRequest;
import potatoes.server.mail.dto.VerifyResponse;
import potatoes.server.mail.service.MailVerificationService;
import potatoes.server.utils.CommonResponse;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "메일", description = "메일 관련 API")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@RestController
public class MailController {

	private final MailVerificationService mailVerificationService;

	@Operation(summary = "메일 전송(회원가입)", description = "회원가입용 메일 전송, 이메일이 존재하면 예외터트림")
	@PostMapping("/sign-up/emails")
	public ResponseEntity<CommonResponse<?>> sendSignupVerificationEmail(@RequestBody @Valid SendMailRequest request) {
		mailVerificationService.sendSignupEmail(request);
		return ResponseEntity.ok().body(CommonResponse.create());
	}

	@Operation(summary = "메일 전송(비밀번호찾기)", description = "비밀번호용 메일 전송, 이메일이 존재하지 않으면 예외터트림")
	@PostMapping("/password/emails")
	public ResponseEntity<CommonResponse<?>> validateEmailExistsForPasswordReset(
		@RequestBody @Valid SendMailRequest request,
		@Authorization @Parameter(hidden = true) Long userid
	) {
		mailVerificationService.sendPasswordResetEmail(request);
		return ResponseEntity.ok().body(CommonResponse.create());
	}

	@Operation(summary = "인증번호 확인", description = "인증번호 유효시간은 5분, 반환에 인증토큰 바디로 날라옴")
	@PostMapping("emails/verify")
	public ResponseEntity<CommonResponse<VerifyResponse>> verifyNumber(
		@RequestBody @Valid EmailVerifyRequest request
	) {
		VerifyResponse verifyResponse = mailVerificationService.verifyNumberAndCreateToken(request.verifyNumber(),
			request.email());
		return ResponseEntity.ok().body(CommonResponse.from(verifyResponse));
	}
}
