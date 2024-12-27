package potatoes.server.controller;

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
import potatoes.server.dto.CommonResponse;
import potatoes.server.dto.EmailVerifyRequest;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.dto.VerifyResponse;
import potatoes.server.service.MailVerificationService;
import potatoes.server.utils.annotation.Authorization;

@Tag(name = "Mail", description = "Mail API")
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

	/***
	 *
	 * TODO 개선점으로 인가를 진행할때 유저의 id를 필요로 하지 않는 경우에 대한 메서드 상단 위치에 올수있는 인가 어노테이션을 추가하고 반환을 받지 않는 방식을
	 *  구현해보면 어떨가 생각합니다. 지금처럼 인터셉터나 혹은 AOP를 사용해봐도 재밌을것 같습니다.
	 *
	 */

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
