package potatoes.server.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SignInRequest;
import potatoes.server.dto.SignUpRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.DuplicationEmail;
import potatoes.server.error.exception.InvalidSignInInformation;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.crypto.TokenCookie;
import potatoes.server.utils.jwt.JwtTokenUtil;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenUtil jwtTokenUtil;

	public ResponseCookie signIn(SignInRequest request) {
		User getUser = userRepository.findByEmail(request.email()).orElseThrow(InvalidSignInInformation::new);

		boolean isPasswordMatched = passwordEncoder.matches(request.password(), getUser.getPassword());
		if (!isPasswordMatched) {
			throw new InvalidSignInInformation();
		}
		String accessToken = jwtTokenUtil.createToken(getUser.getId().toString());

		return new TokenCookie(accessToken).generateCookie();
	}

	@Transactional
	public void signUp(SignUpRequest request) {
		userRepository.findByEmail(request.email()).orElseThrow(DuplicationEmail::new);

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
}
