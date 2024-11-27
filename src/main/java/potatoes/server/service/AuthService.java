package potatoes.server.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.CreateUserRequest;
import potatoes.server.entity.User;
import potatoes.server.error.exception.AlreadyExistsEmailException;
import potatoes.server.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;

	@Transactional
	public void register(CreateUserRequest request) {

		Optional<User> optionalUser = userRepository.findByEmail(request.email());
		if (optionalUser.isPresent()) {
			throw new AlreadyExistsEmailException();
		}

		User user = User.builder()
			.email(request.email())
			.password(request.password())
			.name(request.name())
			.companyName(request.companyName())
			.build();
		userRepository.save(user);
	}
}
