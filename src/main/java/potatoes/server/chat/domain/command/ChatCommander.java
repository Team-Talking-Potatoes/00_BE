package potatoes.server.chat.domain.command;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import potatoes.server.chat.entity.Chat;
import potatoes.server.chat.repository.ChatRepository;
import potatoes.server.travel.entity.Travel;
import potatoes.server.user.entity.User;

@RequiredArgsConstructor
@Component
public class ChatCommander {

	private final ChatRepository chatRepository;

	public void createChat(Travel travel, User host) {
		Chat chat = Chat.createTravelChat(travel, host);
		chatRepository.save(chat);
	}
}
