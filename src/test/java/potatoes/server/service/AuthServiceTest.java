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
import potatoes.server.entity.User;
import potatoes.server.exception.AlreadyExistsEmailException;
import potatoes.server.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	@Test
	void 회원가입시_중복된_이메일이_존재하는_경우_AlreadyExistsEmailException_반환() {
		// given
		User existingUser = User.builder()
			.email("test@example.com")
			.name("user1")
			.password("password")
			.companyName("Test Company")
			.build();
		when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));
		CreateUserRequest newUserRequest = new CreateUserRequest("test@example.com", "password", "Test User",
			"Test Company");

		// expect
		assertThatThrownBy(() -> authService.register(newUserRequest))
			.isInstanceOf(AlreadyExistsEmailException.class);
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