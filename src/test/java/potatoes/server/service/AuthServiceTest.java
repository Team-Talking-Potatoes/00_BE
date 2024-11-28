package potatoes.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import potatoes.server.dto.CreateUserRequest;
import potatoes.server.dto.SignInUserRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.AlreadyExistsEmail;
import potatoes.server.error.exception.InvalidSignInInformation;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtTokenUtil jwtTokenUtil;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void 회원가입_성공() {
		// given
		CreateUserRequest newUserRequest = new CreateUserRequest("test@example.com", "password", "Test User",
			"Test Company");

		// expect
		authService.register(newUserRequest);
	}

	@Test
	void 회원가입시_중복된_이메일이_존재하는_경우_AlreadyExistsEmailException() {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password("encodedPassword")
			.companyName("Test Company")
			.build();
		when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));
		CreateUserRequest newUserRequest = new CreateUserRequest("test@example.com", "password", "Test User",
			"Test Company");

		// expect
		assertThatThrownBy(() -> authService.register(newUserRequest))
			.isInstanceOf(AlreadyExistsEmailException.class);
			.isInstanceOf(AlreadyExistsEmail.class);
		verify(userRepository).findByEmail("test@example.com");
		verifyNoInteractions(jwtTokenUtil);
	}
	@Test
	void 로그인_성공() {
		// given
		SignInUserRequest request = new SignInUserRequest("test@example.com", "password");
		User user = User.builder()
			.email("test@example.com")
			.password("encodedPassword")
			.name("Test User")
			.companyName("Test Company")
			.build();
		User spyUser = Mockito.spy(user);
		doReturn(1L).when(spyUser).getId();

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(spyUser));
		when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
		when(jwtTokenUtil.createToken("1")).thenReturn("token");

		// when
		String result = authService.signIn(request);

		// then
		assertThat(result).isEqualTo("token");
		verify(userRepository).findByEmail("test@example.com");
		verify(passwordEncoder).matches("password", "encodedPassword");
		verify(jwtTokenUtil).createToken("1");
		verify(spyUser).getId();
	}

	@Test
	void 존재하지_않는_이메일로_로그인_시도할_경우_InvalidSignInInformation() {
		// given
		SignInUserRequest request = new SignInUserRequest("invalid@example.com", "password");
		when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

		// expect
		assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(InvalidSignInInformation.class);
		verify(userRepository).findByEmail("invalid@example.com");
		verifyNoInteractions(passwordEncoder);
		verifyNoInteractions(jwtTokenUtil);
	}

	@Test
	void 잘못된_비밀번호로_로그인_시도할_경우_InvalidSignInInformation() {
		// given
		SignInUserRequest request = new SignInUserRequest("test@example.com", "wrongPassword");
		User user = User.builder()
			.email("test@example.com")
			.password("encodedPassword")
			.name("Test User")
			.companyName("Test Company")
			.build();
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

		// expect
		assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(InvalidSignInInformation.class);
		verify(userRepository).findByEmail("test@example.com");
		verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
	}
	}

	@Test
	void 회원가입_성공() {
		// given
		CreateUserRequest newUserRequest = new CreateUserRequest("test@example.com", "password", "Test User",
			"Test Company");

		// expect
		authService.register(newUserRequest);
	}
}