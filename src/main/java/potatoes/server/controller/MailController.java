package potatoes.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
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
}
