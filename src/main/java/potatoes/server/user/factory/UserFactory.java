package potatoes.server.user.factory;

import static potatoes.server.utils.error.ErrorCode.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.user.entity.User;
import potatoes.server.user.repository.UserRepository;
import potatoes.server.utils.error.exception.WeGoException;

@RequiredArgsConstructor
@Component
public class UserFactory {

	private final UserRepository userRepository;

	public User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
	}
}
