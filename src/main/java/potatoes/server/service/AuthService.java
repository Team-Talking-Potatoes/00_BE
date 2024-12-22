package potatoes.server.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SignInRequest;
import potatoes.server.dto.SignUpRequest;
import potatoes.server.dto.UnauthorizedPasswordResetRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.DuplicationEmail;
import potatoes.server.error.exception.InvalidSignInInformation;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.crypto.TokenCookie;
import potatoes.server.utils.jwt.JwtTokenUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenUtil jwtTokenUtil;
	private final TokenCookie tokenCookie;

	public ResponseCookie signIn(SignInRequest request) {
		User getUser = userRepository.findByEmail(request.email()).orElseThrow(InvalidSignInInformation::new);

		boolean isPasswordMatched = passwordEncoder.matches(request.password(), getUser.getPassword());
		if (!isPasswordMatched) {
			throw new InvalidSignInInformation();
		}
		String accessToken = jwtTokenUtil.createToken(getUser.getId().toString());

		return tokenCookie.generateCookie(accessToken);
	}

	@Transactional
	public void signUp(SignUpRequest request) {
		validateEmailNotExists(request.email());
		//TODO 전화번호 중복 허용? 에대해서는 보류해야 할듯합니다.
		User createdUser = User.builder()
			.email(request.email())
			.password(passwordEncoder.encrypt(request.password()))
			.name(request.name())
			.nickname(request.nickname())
			.birthDate(Integer.parseInt(request.birthDate()))
			.contact(request.contact())
			.build();

		userRepository.save(createdUser);
	}

	@Transactional
	public void unauthorizedPasswordReset(UnauthorizedPasswordResetRequest request) {
		User getUser = userRepository.findByEmail(request.email()).orElseThrow(UserNotFound::new);

		String newPassword = passwordEncoder.encrypt(request.newPassword());
		getUser.resetPassword(newPassword);
	}

	public void validateEmailNotExists(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new DuplicationEmail();
		}
	}

	public void validateEmailExists(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new UserNotFound();
		}
	}
}
