package potatoes.server.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.dto.VerifyResponse;
import potatoes.server.error.exception.MailVerifyNumberExpired;
import potatoes.server.error.exception.MailVerifyNumberNotValid;
import potatoes.server.utils.GenerateRandomNumber;
import potatoes.server.utils.jwt.JwtTokenUtil;
import potatoes.server.utils.redis.RedisVerificationStore;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MailVerificationService {

	private final AuthService authService;
	private final CustomMailSender customMailSender;
	private final RedisVerificationStore redisStore;
	private final JwtTokenUtil jwtTokenUtil;
	private static final Duration EXPIRATION = Duration.ofMinutes(5);

	@Transactional
	public void sendSignupEmail(SendMailRequest request) {
		authService.validateEmailNotExists(request.email());
		String verifyNumber = createAndStoreVerificationNumber(request.email());
		customMailSender.sendVerificationMail(request, verifyNumber);
	}

	@Transactional
	public void sendPasswordResetEmail(SendMailRequest request) {
		authService.validateEmailExists(request.email());
		String verifyNumber = createAndStoreVerificationNumber(request.email());
		customMailSender.sendVerificationMail(request, verifyNumber);
	}

	public VerifyResponse verifyNumberAndCreateToken(String verifyNumber, String email) {
		verifyNumberByEmail(verifyNumber, email);
		return createAccessToken();
	}

	private String createAndStoreVerificationNumber(String email) {
		String verifyNumber = GenerateRandomNumber.generateNumber();
		redisStore.store(email, verifyNumber, EXPIRATION);
		return verifyNumber;
	}

	private void verifyNumberByEmail(String verifyNumber, String email) {
		String storedNumber = redisStore.find(email);
		if (storedNumber == null) {
			throw new MailVerifyNumberExpired();
		}

		if (!storedNumber.equals(verifyNumber)) {
			throw new MailVerifyNumberNotValid();
		}
		redisStore.remove(email);
	}

	private VerifyResponse createAccessToken() {
		return new VerifyResponse(
			jwtTokenUtil.createToken(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
	}
}
