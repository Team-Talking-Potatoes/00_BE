package potatoes.server.utils.stomp;

import static potatoes.server.error.ErrorCode.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.entity.ChatUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.repository.ChatRepository;
import potatoes.server.repository.ChatUserRepository;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatEventHandler {

	private final ChatUserRepository chatUserRepository;
	private final ChatRepository chatRepository;
	private final JwtTokenUtil jwtTokenProvider;

	public void handleSubscribe(StompHeaderAccessor headerAccessor) {
		StompUserPrincipal user = (StompUserPrincipal)headerAccessor.getUser();
		String destination = headerAccessor.getDestination();
		log.info(destination);
		String[] split = destination.substring(1).split("/");

		log.info(Arrays.toString(split));
		long chatId;
		if (split.length == 3 && !split[2].equals("read")) {
			chatId = Long.parseLong(split[2]);
		} else if (split.length == 4 && split[2].equals("read")) {
			log.info(split[3]);
			chatId = Long.parseLong(split[3]);
		} else {
			throw new WeGoException(STOMP_SUBSCRIBE_FAILED);
		}

		if (!chatRepository.existsById(chatId)) {
			throw new WeGoException(CHAT_NOT_FOUND);
		}

		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User sender = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(user.getUserId())) {
				sender = chatUser.getUser();
				break;
			}
		}
		if (sender == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}
	}

	public void handleConnect(StompHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		String accessToken = (String)sessionAttributes.get("accessToken");
		if (accessToken == null) {
			throw new WeGoException(TOKEN_NOT_FOUND);
		}

		if (!jwtTokenProvider.validateToken(accessToken)) {
			throw new WeGoException(UNAUTHORIZED);
		}

		long userId = Long.parseLong(jwtTokenProvider.getPayload(accessToken));
		headerAccessor.setUser(new StompUserPrincipal(userId, headerAccessor.getSessionId()));
	}
}
