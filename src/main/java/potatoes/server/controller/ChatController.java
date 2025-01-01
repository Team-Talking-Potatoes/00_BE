package potatoes.server.controller;

import static org.springframework.http.MediaType.*;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ChatSortType;
import potatoes.server.dto.ChatOverviewResponse;
import potatoes.server.dto.ChatSummaryResponse;
import potatoes.server.dto.CommonResponse;
import potatoes.server.dto.MarkAsReadPublish;
import potatoes.server.dto.MessagePublish;
import potatoes.server.dto.RecentChatResponse;
import potatoes.server.service.ChatService;
import potatoes.server.utils.annotation.Authorization;
import potatoes.server.utils.stomp.StompUserPrincipal;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final ChatService chatService;

	@MessageMapping("/chat/{chatId}")
	public void sendMessage(@DestinationVariable Long chatId, MessagePublish message, Principal principal) {
		StompUserPrincipal stompUserPrincipal = (StompUserPrincipal)principal;
		chatService.send(chatId, message, stompUserPrincipal);
	}

	@MessageMapping("/chat/read/{chatId}")
	public void markAsRead(@DestinationVariable Long chatId, MarkAsReadPublish markAsReadPublish, Principal principal) {
		StompUserPrincipal stompUserPrincipal = (StompUserPrincipal)principal;
		chatService.markAsRead(chatId, markAsReadPublish.chatMessageId(), stompUserPrincipal);
	}

	@Operation(summary = "채팅 기록 조회")
	@GetMapping("/chat/{chatId}")
	public ResponseEntity<CommonResponse<RecentChatResponse>> getChatHistoryList(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable("chatId") Long chatId,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "latest", defaultValue = "0") Long latestChatId
	) {
		return ResponseEntity.ok(
			CommonResponse.from(chatService.getRecentChatMessages(userId, chatId, size, latestChatId)));
	}

	@Operation(summary = "채팅방 목록 불러오기", description = "현재 참여중인 채팅방 목록을 조회합니다.")
	@GetMapping("/chat")
	public ResponseEntity<CommonResponse<List<ChatSummaryResponse>>> getChatSummaryList(
		@Authorization @Parameter(hidden = true) Long userId,
		@RequestParam(defaultValue = "UNREAD") ChatSortType sortType
	) {
		return ResponseEntity.ok(CommonResponse.from(chatService.getChatSummaryList(userId, sortType)));
	}

	@Operation(summary = "채팅방 입장 요청")
	@PostMapping("/chat/{chatId}")
	public ResponseEntity<CommonResponse<?>> joinChat(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable("chatId") Long chatId
	) {
		chatService.joinChat(userId, chatId);
		return ResponseEntity.ok(CommonResponse.create());
	}

	@Operation(summary = "채팅방 이미지 업로드")
	@PostMapping(path = "/chat/{chatId}/image", consumes = MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<List<String>>> imageUpload(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable("chatId") Long chatId,
		@RequestParam("files") List<MultipartFile> files
	) {
		return ResponseEntity.ok(CommonResponse.from(chatService.updateChatImages(userId, chatId, files)));
	}

	@Operation(summary = "채팅방 정보 모아보기")
	@GetMapping("/chat/{chatId}/overview")
	public ResponseEntity<CommonResponse<ChatOverviewResponse>> getChatOverview(
		@Authorization @Parameter(hidden = true) Long userId,
		@PathVariable("chatId") Long chatId
	) {
		return ResponseEntity.ok(CommonResponse.from(chatService.getChatOverview(userId, chatId)));
	}
}
