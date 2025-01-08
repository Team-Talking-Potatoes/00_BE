package potatoes.server.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import potatoes.server.chat.entity.Chat;
import potatoes.server.entity.Travel;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	@Query("""
		SELECT c FROM Chat c
		JOIN FETCH c.travel
		JOIN FETCH c.host
		WHERE c.travel = :travel
		""")
	Optional<Chat> findByTravel(@Param("travel") Travel travel);

	@Query("""
		SELECT c FROM Chat c
		JOIN FETCH c.travel
		WHERE c.id = :chatId
		""")
	Optional<Chat> findChatFetchJoinTravel(@Param("chatId") Long chatId);

	@Query("""
		SELECT c FROM Chat c
		JOIN FETCH c.travel
		WHERE c.travel.id = :travelId
		""")
	Optional<Chat> findByIdFetchJoinTravel(@Param("travelId") Long travelId);
}
