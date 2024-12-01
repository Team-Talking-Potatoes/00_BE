package potatoes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import potatoes.server.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
