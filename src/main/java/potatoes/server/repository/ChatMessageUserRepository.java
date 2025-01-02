package potatoes.server.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.ChatMessage;
import potatoes.server.entity.ChatMessageUser;

public interface ChatMessageUserRepository extends JpaRepository<ChatMessageUser, Long> {

	@Query("""
		SELECT c FROM ChatMessageUser c
		WHERE c.chatMessage.id = :chatMessageId
		AND c.user.id = :userId
		""")
	Optional<ChatMessageUser> findByChatMessageAndUser(@Param("chatMessageId") Long chatMessageId,
		@Param("userId") Long userId);

	long countByChatMessageAndHasReadIsTrue(ChatMessage chatMessage);

	long countByUserIdAndChatIdAndHasReadIsFalse(Long userId, Long chatId);

	long countByUserIdAndChatId(Long userId, Long chatId);

	@Query("""
		SELECT c FROM ChatMessageUser c
		WHERE c.chat.id = :chatId
		AND c.user.id = :userId
		AND c.hasRead = false
		""")
	List<ChatMessageUser> findAllUnReadMessageByUserIdAndChatId(
		@Param("userId") Long userId,
		@Param("chatId") Long chatId
	);

	@Query("""
		SELECT COUNT(c) FROM ChatMessageUser c
		WHERE c.chat.id = :chatId
		AND c.user.id = :userId
		AND c.hasRead = false
		""")
	long countUserUnReadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

	@Query("""
		SELECT cm.id, COUNT(cmu) FROM ChatMessageUser cmu
		RIGHT JOIN cmu.chatMessage cm 
		WHERE cm.id IN :messageIds 
		AND cmu.hasRead = false 
		GROUP BY cm.id
		""")
	Map<Long, Long> countUnreadByMessageIds(@Param("messageIds") List<Long> messageIds);
}
