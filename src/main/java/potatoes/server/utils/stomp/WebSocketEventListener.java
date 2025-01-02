package potatoes.server.utils.stomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.service.ChatService;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

	private final ChatService chatService;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		log.info("새로운 웹소켓 연결 감지, {}", event.toString());
	}

	@EventListener
	public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
		log.info("채팅방 구독 감지, {}", event.toString());
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = headerAccessor.getDestination();
		String[] split = destination.substring(1).split("/");
		StompUserPrincipal user = (StompUserPrincipal)headerAccessor.getUser();
		if (split.length == 3 && !split[2].equals("read")) {
			chatService.readAllUnReadChatMessages(Long.valueOf(split[2]), user.getUserId());
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		log.info("연결이 종료되었습니다, event = {}, headerAccessor = {}", event, headerAccessor);
	}

}

