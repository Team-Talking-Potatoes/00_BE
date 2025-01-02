package potatoes.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	@Query("SELECT cm FROM ChatMessage cm WHERE cm.chat.id = :chatId ORDER BY cm.createdAt DESC LIMIT 1")
	Optional<ChatMessage> findLatestMessageByChatId(@Param("chatId") Long chatId);

	@Query("SELECT COUNT(cm) FROM ChatMessage cm")
	long countAll();

	@Query("""
		SELECT cm FROM ChatMessage cm
		JOIN FETCH cm.chat
		JOIN FETCH cm.sender
		WHERE cm.chat.id = :chatId
		AND cm.id < :messageId ORDER BY cm.id DESC""")
	List<ChatMessage> findPreviousMessages(
		@Param("chatId") Long chatId,
		@Param("messageId") Long messageId,
		Pageable pageable
	);

	List<ChatMessage> findByChatIdAndIdLessThanOrderByIdDesc(Long chatId, Long messageId, Pageable pageable);
}
