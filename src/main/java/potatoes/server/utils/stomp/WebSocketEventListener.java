package potatoes.server.utils.stomp;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
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
	private final SimpMessageSendingOperations messagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		log.info("새로운 웹소켓 연결 감지, {}", event.toString());
	}

	@EventListener
	public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
		log.info("채팅방 구독 감지, {}", event.toString());
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = headerAccessor.getDestination();
		String[] split = headerAccessor.getDestination().substring(1, destination.length()).split("/");

		StompUserPrincipal user = (StompUserPrincipal)headerAccessor.getUser();
		try {
			if (split.length == 3 && !split[2].equals("read")) {
				chatService.checkIsUserHasJoined(Long.valueOf(split[2]), user.getUserId(), true);
			} else if (split[2].equals("read")) {
				chatService.checkIsUserHasJoined(Long.valueOf(split[3]), user.getUserId(), false);
			}
		} catch (Exception e) {
			String sessionId = headerAccessor.getSessionId();

			// 에러 메시지 전송
			messagingTemplate.convertAndSendToUser(
				sessionId,
				"/queue/errors",
				"Unauthorized access"
			);

			// 연결 종료를 위한 새로운 StompHeaderAccessor 생성
			StompHeaderAccessor disconnectHeaderAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
			disconnectHeaderAccessor.setSessionId(sessionId);

			messagingTemplate.convertAndSend(
				destination,
				disconnectHeaderAccessor.getMessageHeaders()
			);
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		log.info("연결이 종료되었습니다, event = {}, sessionId = {}", event, sessionId);
	}
}
