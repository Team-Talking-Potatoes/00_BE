package potatoes.server.mail.service;

import static potatoes.server.utils.error.ErrorCode.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.auth.service.AuthService;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.dto.VerifyResponse;
import potatoes.server.infra.redis.RedisStore;
import potatoes.server.utils.GenerateRandomNumber;
import potatoes.server.utils.error.exception.WeGoException;
import potatoes.server.utils.jwt.JwtTokenUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MailVerificationService {

	private final AuthService authService;
	private final CustomMailSender customMailSender;
	private final RedisStore redisStore;
	private final JwtTokenUtil jwtTokenUtil;
	private static final Duration EXPIRATION = Duration.ofMinutes(5);
	private static final String EMAIL_VERIFY_PREFIX = "EMAIL:VERIFY:";

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
		redisStore.store(EMAIL_VERIFY_PREFIX + email, verifyNumber, EXPIRATION);
		return verifyNumber;
	}

	private void verifyNumberByEmail(String verifyNumber, String email) {
		String storedNumber = redisStore.find(EMAIL_VERIFY_PREFIX + email);
		if (storedNumber == null) {
			throw new WeGoException(VERIFY_NUMBER_EXPIRED);
		}

		if (!storedNumber.equals(verifyNumber)) {
			throw new WeGoException(VERIFY_NUMBER_NOT_VALID);
		}
		redisStore.remove(EMAIL_VERIFY_PREFIX + email);
	}

	private VerifyResponse createAccessToken() {
		return new VerifyResponse(
			jwtTokenUtil.createAccessToken(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
	}
}
