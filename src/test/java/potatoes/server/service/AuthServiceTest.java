package potatoes.server.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import potatoes.server.dto.CreateUserRequest;
import potatoes.server.dto.GetUserResponse;
import potatoes.server.dto.SignInUserRequest;
import potatoes.server.dto.UpdateUserRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.AlreadyExistsEmail;
import potatoes.server.error.exception.InvalidSignInInformation;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;
import potatoes.server.utils.jwt.JwtTokenUtil;

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

	@Test
	void 회원_정보_조회_성공() {
		// given
		User user = User.builder()
			.email("test@example.com")
			.password("encodedPassword")
			.name("Test User")
			.companyName("Test Company")
			.build();
		Instant mockCreatedAt = Instant.now();
		User spyUser = Mockito.spy(user);
		doReturn(1L).when(spyUser).getId();
		doReturn(mockCreatedAt).when(spyUser).getCreatedAt();
		doReturn(mockCreatedAt).when(spyUser).getUpdatedAt();
		when(userRepository.findById(1L)).thenReturn(Optional.of(spyUser));

		// when
		GetUserResponse result = authService.find(1L);

		// then
		assertThat(result.email()).isEqualTo(user.getEmail());
		assertThat(result.name()).isEqualTo(user.getName());
		assertThat(result.companyName()).isEqualTo(user.getCompanyName());
		assertThat(result.createdAt()).isEqualTo(mockCreatedAt.toEpochMilli());
		assertThat(result.updatedAt()).isEqualTo(mockCreatedAt.toEpochMilli());
		verify(userRepository).findById(1L);
	}

	@Test
	void 회원_정보_조회_존재하지_않는_ID_로_조회할_경우_UserNotFoundException() {
		// given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// expect
		assertThatThrownBy(() -> authService.find(userId)).isInstanceOf(UserNotFound.class);
	}

	@Test
	void 회원_정보_수정_성공() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.email("test@example.com")
			.password("encodedPassword")
			.name("Test User")
			.companyName("Test Company")
			.build();
		User spyUser = Mockito.spy(user);
		doReturn(userId).when(spyUser).getId();
		Instant mockCreatedAt = Instant.now();
		doReturn(mockCreatedAt).when(spyUser).getCreatedAt();
		doReturn(mockCreatedAt).when(spyUser).getUpdatedAt();
		UpdateUserRequest request = new UpdateUserRequest("newCompanyName", "newImage");
		when(userRepository.findById(userId)).thenReturn(Optional.of(spyUser));

		// when
		GetUserResponse response = authService.update(userId, request);

		// then
		assertThat(response.companyName()).isEqualTo(request.companyName());
		assertThat(response.image()).isEqualTo(request.image());
	}

	@Test
	void 회원_정보_수정_존재하지_않는_ID_로_조회할_경우_UserNotFoundException() {
		// given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		UpdateUserRequest request = new UpdateUserRequest("newCompanyName", "newImage");

		// expect
		assertThatThrownBy(() -> authService.update(userId, request)).isInstanceOf(UserNotFound.class);
	}
}