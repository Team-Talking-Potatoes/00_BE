package potatoes.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.ChatUser;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

	@Query("""
			SELECT c FROM ChatUser c
			JOIN FETCH c.user
			JOIN FETCH c.chat
			WHERE c.chat.id = :chatId
		""")
	List<ChatUser> findAllChatUserByChatID(@Param("chatId") Long chatId);

	@Query("""
		SELECT c FROM ChatUser c
					JOIN FETCH c.user
					JOIN FETCH c.chat
					WHERE c.user.id = :userId
		""")
	List<ChatUser> findAllByUserId(@Param("userId") Long userId);

	boolean existsByUserIdAndChatId(Long userId, Long chatId);
}
