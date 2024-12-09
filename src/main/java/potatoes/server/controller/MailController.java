package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.service.MailService;

@Tag(name = "Mail", description = "Mail API")
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@RestController
public class MailController {

	private final MailService mailService;

	@Operation(summary = "메일 전송", description = "인증번호 메일전송")
	@PostMapping("/emails")
	public ResponseEntity<Void> sendMail(@RequestBody @Valid SendMailRequest request) {
		mailService.sendMail(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "인증번호 확인", description = "인증번호 유효시간은 4분으로 잡았지만 프론트단에서 3분으로 막아주세요 (서버처리 시간때문에 넉넉히 1분 더잡았습니다.)")
	@GetMapping("/emails/verify")
	public ResponseEntity<Void> verifyNumber(
		@RequestParam @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다.") String verifyNumber,
		@RequestParam @Email String email
	) {
		mailService.verifyNumberByEmail(verifyNumber, email);
		return ResponseEntity.ok().build();
	}
}
