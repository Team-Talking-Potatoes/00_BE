package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.EmailVerifyRequest;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.dto.VerifyResponse;
import potatoes.server.service.MailVerificationService;

@Tag(name = "Mail", description = "Mail API")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@RestController
public class MailController {

	private final MailVerificationService mailVerificationService;

	@Operation(summary = "메일 전송(회원가입)", description = "회원가입용 메일 전송, 이메일이 존재하면 예외터트림")
	@PostMapping("/sign-up/emails")
	public ResponseEntity<Void> sendSignupVerificationEmail(@RequestBody @Valid SendMailRequest request) {
		mailVerificationService.sendSignupEmail(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "메일 전송(비밀번호찾기)", description = "비밀번호용 메일 전송, 이메일이 존재하지 않으면 예외터트림")
	@PostMapping("/password/emails")
	public ResponseEntity<Void> validateEmailExistsForPasswordReset(@RequestBody @Valid SendMailRequest request) {
		mailVerificationService.sendPasswordResetEmail(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "인증번호 확인", description = "인증번호 유효시간은 5분, 반환에 인증토큰 바디로 날라옴")
	@PostMapping("emails/verify")
	public ResponseEntity<VerifyResponse> verifyNumber(
		@RequestBody @Valid EmailVerifyRequest request
	) {
		VerifyResponse verifyResponse = mailVerificationService.verifyNumberAndCreateToken(request.verifyNumber(), request.email());
		return ResponseEntity.ok().body(verifyResponse);
	}
}
