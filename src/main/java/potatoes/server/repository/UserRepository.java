package potatoes.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
