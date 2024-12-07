package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.service.MailService;

@Tag(name = "Mail", description = "Mail API")
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class MailController {

	private final MailService mailService;

	@PostMapping("/mail-send")
	public ResponseEntity<Void> sendMail(@RequestBody SendMailRequest request) {
		mailService.sendMail(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/mail-check")
	public ResponseEntity<Void> verifyNumber(
		@RequestParam @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는 6자리 숫자여야 합니다.") String verifyNumber,
		@RequestParam @Email String email
	) {
		mailService.verifyNumberByEmail(verifyNumber, email);
		return ResponseEntity.ok().build();
	}
}
