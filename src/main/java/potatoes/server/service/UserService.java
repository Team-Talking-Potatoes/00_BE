package potatoes.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.ResetPasswordRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.PasswordMismatch;
import potatoes.server.error.exception.UserNotFound;
import potatoes.server.repository.UserRepository;
import potatoes.server.utils.crypto.PasswordEncoder;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void resetPassword(ResetPasswordRequest request, Long userId) {
		User getUser = userRepository.findById(userId).orElseThrow(UserNotFound::new);
		boolean isPasswordMatched = passwordEncoder.matches(request.currentPassword(), getUser.getPassword());

		if (!isPasswordMatched) {
			throw new PasswordMismatch();
		}

		String newPassword = passwordEncoder.encrypt(request.newPassword());

		getUser.resetPassword(newPassword);
	}
}
