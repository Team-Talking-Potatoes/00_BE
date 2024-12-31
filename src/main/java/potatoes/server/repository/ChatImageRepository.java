package potatoes.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.entity.ChatImage;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {

	@Query("""
		SELECT ci FROM ChatImage ci
		WHERE ci.imageUrl = :imageUrl
		""")
	Optional<ChatImage> findByImageUrl(@Param("imageUrl") String imageUrl);

	@Query("""
		SELECT ci FROM ChatImage ci
		JOIN FETCH ci.chatMessage
		JOIN FETCH ci.chatMessage.sender
		WHERE ci.chatMessage.chat.id = :chatId
		""")
	List<ChatImage> findAllByChat(@Param("chatId") Long chatId);
}
