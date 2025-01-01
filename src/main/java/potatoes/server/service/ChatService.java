package potatoes.server.service;

import static java.util.Comparator.*;
import static potatoes.server.error.ErrorCode.*;
import static potatoes.server.utils.time.DateTimeUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.joda.time.Instant;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.constant.ChatSortType;
import potatoes.server.dto.ChatAlbumResponse;
import potatoes.server.dto.ChatOverviewResponse;
import potatoes.server.dto.ChatSummaryResponse;
import potatoes.server.dto.MarkAsReadSubscribe;
import potatoes.server.dto.MessagePublish;
import potatoes.server.dto.MessageSubscribe;
import potatoes.server.dto.ParticipantsInfoResponse;
import potatoes.server.dto.RecentChatResponse;
import potatoes.server.entity.Chat;
import potatoes.server.entity.ChatImage;
import potatoes.server.entity.ChatMessage;
import potatoes.server.entity.ChatMessageUser;
import potatoes.server.entity.ChatUser;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.repository.ChatImageRepository;
import potatoes.server.repository.ChatMessageRepository;
import potatoes.server.repository.ChatMessageUserRepository;
import potatoes.server.repository.ChatRepository;
import potatoes.server.repository.ChatUserRepository;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.utils.s3.S3UtilsProvider;
import potatoes.server.utils.stomp.StompUserPrincipal;
import potatoes.server.utils.time.DateTimeUtils;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatRepository chatRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final ChatMessageUserRepository chatMessageUserRepository;
	private final TravelUserRepository travelUserRepository;
	private final ChatImageRepository chatImageRepository;
	private final S3UtilsProvider s3;

	@Transactional
	public void send(Long chatId, MessagePublish message, StompUserPrincipal principal) {
		Chat chat = chatRepository.findById(chatId).orElseThrow(
			() -> new WeGoException(CHAT_NOT_FOUND)
		);

		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User sender = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(principal.getUserId())) {
				sender = chatUser.getUser();
				break;
			}
		}
		if (sender == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}

		ChatMessage chatMessage = ChatMessage.builder()
			.chat(chat)
			.sender(sender)
			.message(message.message())
			.build();
		chatMessageRepository.save(chatMessage);

		// 채팅 이미지 조회 및 저장
		List<String> chatImages = new ArrayList<>();
		if (message.images().length > 0) {
			String[] StringImageUrlArr = message.message().substring(1, message.message().length() - 1).split(",\\s*");
			for (String imageUrl : StringImageUrlArr) {
				Optional<ChatImage> optionalChatImage = chatImageRepository.findByImageUrl(imageUrl);
				if (optionalChatImage.isPresent()) {
					ChatImage chatImage = optionalChatImage.get();
					chatImage.messageSent(chatMessage);
					chatImageRepository.save(chatImage);
					chatImages.add(chatImage.getImageUrl());
				}
			}
		}

		List<ChatMessageUser> chatMessageUserList = chatUserList.stream()
			.map(chatUser -> {
				ChatMessageUser chatMessageUser = ChatMessageUser.builder()
					.user(chatUser.getUser())
					.chat(chat)
					.chatMessage(chatMessage)
					.build();
				if (chatUser.getUser().getId().equals(principal.getUserId())) {
					chatMessageUser.markAsRead();
				}
				return chatMessageUser;
			})
			.toList();
		chatMessageUserRepository.saveAll(chatMessageUserList);

		messagingTemplate.convertAndSend(
			"/sub/chat/" + chatId,
			MessageSubscribe.of(chatMessage, chatImages, sender, chat.getCurrentMemberCount())
		);
	}

	@Transactional
	public void markAsRead(Long chatId, Long chatMessageId, StompUserPrincipal principal) {
		ChatMessageUser chatMessageUser = chatMessageUserRepository.findByChatMessageAndUser(
			chatMessageId, principal.getUserId()
		).orElseThrow(() -> new WeGoException(CHAT_NOT_FOUND));

		chatMessageUser.markAsRead();

		long totalMembers = chatMessageUser.getChat().getCurrentMemberCount();
		long readCount = chatMessageUserRepository.countByChatMessageAndHasReadIsTrue(
			chatMessageUser.getChatMessage()
		) + 1;

		long unreadCount = totalMembers - readCount;

		messagingTemplate.convertAndSend("/sub/chat/read/" + chatId,
			new MarkAsReadSubscribe(chatMessageId, unreadCount));
	}

	@Transactional
	public void joinChat(Long userId, Long chatId) {
		Chat chat = chatRepository.findChatFetchJoinTravel(chatId).orElseThrow(
			() -> new WeGoException(CHAT_NOT_FOUND)
		);

		TravelUser travelUser = travelUserRepository.findByTravelAndUserJoinFetchUser(chat.getTravel().getId(), userId)
			.orElseThrow(
				() -> new WeGoException(UNABLE_TO_JOIN_CHAT)
			);

		ChatUser chatUser = ChatUser.builder()
			.user(travelUser.getUser())
			.chat(chat)
			.build();
		chatUserRepository.save(chatUser);
	}

	public List<ChatSummaryResponse> getChatSummaryList(Long userId, ChatSortType sortType) {
		// 참여중인 채팅방 카운트
		List<ChatSummaryResponse> joinedChats = chatUserRepository.findAllByUserId(userId).stream()
			.map(chatUser -> {
				long unreadMessages = chatMessageUserRepository.countByUserIdAndChatIdAndHasReadIsFalse(userId,
					chatUser.getChat().getId());
				ChatMessage chatMessage = chatMessageRepository.findLatestMessageByChatId(chatUser.getChat().getId())
					.orElseGet(() -> new ChatMessage(chatUser.getChat(), null, ""));
				return ChatSummaryResponse.of(
					chatUser.getChat(),
					true,
					unreadMessages,
					chatMessage.getCreatedAt() == null ? Instant.now().toString() :
						getYearMonthDayTime(chatMessage.getCreatedAt()));
			}).toList();

		// 참여할 수 있는 채팅방 메시지 카운트
		List<ChatSummaryResponse> availableChats = travelUserRepository.findAllByUserId(userId).stream()
			.map(travelUser -> {
				Chat chat = chatRepository.findByTravel(travelUser.getTravel())
					.orElseThrow(() -> new WeGoException(CHAT_NOT_FOUND));

				if (chatUserRepository.existsByUserIdAndChatId(
					travelUser.getUser().getId(),
					chat.getId())) {
					return null;
				}

				long unreadMessages = chatMessageUserRepository.countByUserIdAndChatId(chat.getHost().getId(),
					chat.getId());
				ChatMessage chatMessage = chatMessageRepository.findLatestMessageByChatId(chat.getId())
					.orElseGet(() -> new ChatMessage(chat, null, ""));

				return ChatSummaryResponse.of(
					chat,
					false,
					unreadMessages,
					chatMessage.getCreatedAt() == null ? Instant.now().toString() :
						getYearMonthDayTime(chatMessage.getCreatedAt())
				);
			})
			.filter(Objects::nonNull)
			.toList();

		List<ChatSummaryResponse> result = new ArrayList<>();
		result.addAll(joinedChats);
		result.addAll(availableChats);

		if (sortType.equals(ChatSortType.UNREAD)) {
			result.sort(comparingLong((ChatSummaryResponse chatSummary) ->
				chatSummary.hasJoined() ?
					chatMessageUserRepository.countUserUnReadMessages(chatSummary.chatId(), userId) :
					chatMessageRepository.countAll()
			).reversed());

		} else {
			result.sort(comparing(msg -> parseYearMonthDayTime(msg.lastMessageTime()), reverseOrder()));
		}
		return result;
	}

	@Transactional
	public List<String> updateChatImages(Long userId, Long chatId, List<MultipartFile> files) {
		// 유저가 채팅에 참여했는지 여부 체크
		chatRepository.findById(chatId).orElseThrow(() -> new WeGoException(CHAT_NOT_FOUND));
		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User sender = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(userId)) {
				sender = chatUser.getUser();
				break;
			}
		}
		if (sender == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}

		List<String> fileNames = s3.uploadFiles(files);
		List<String> urlList = fileNames.stream()
			.map(s3::getFileUrl)
			.toList();

		List<ChatImage> chatImages = urlList.stream()
			.map(ChatImage::new)
			.toList();
		chatImageRepository.saveAll(chatImages);

		return urlList;
	}

	public RecentChatResponse getRecentChatMessages(Long userId, Long chatId, int size, Long latestChatId) {
		Chat chat = chatRepository.findById(chatId).orElseThrow(
			() -> new WeGoException(CHAT_NOT_FOUND)
		);
		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User sender = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(userId)) {
				sender = chatUser.getUser();
				break;
			}
		}
		if (sender == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}

		if (latestChatId.equals(0L)) {
			chatId = chatMessageRepository.countAll();
		}

		Pageable pageable = PageRequest.of(0, size);
		List<MessageSubscribe> messageSubscribes = chatMessageRepository.findPreviousMessages(chatId,
				latestChatId, pageable)
			.stream()
			.map(chatMessage -> {
				List<String> images = chatImageRepository.findAllByChat(chatMessage.getChat().getId())
					.stream()
					.map(ChatImage::getImageUrl)
					.toList();
				long totalMembers = chat.getCurrentMemberCount();
				long readCount = chatMessageUserRepository.countByChatMessageAndHasReadIsTrue(chatMessage);
				long unreadCount = totalMembers - readCount;
				return MessageSubscribe.of(chatMessage, images, chatMessage.getSender(), (int)unreadCount);
			})
			.toList();
		return new RecentChatResponse(chat.getName(), messageSubscribes);
	}

	public ChatOverviewResponse getChatOverview(Long userId, Long chatId) {
		Chat chat = chatRepository.findById(chatId).orElseThrow(
			() -> new WeGoException(CHAT_NOT_FOUND)
		);
		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User user = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(userId)) {
				user = chatUser.getUser();
				break;
			}
		}
		if (user == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}

		List<ParticipantsInfoResponse> participantsInfoResponses = chatUserList.stream()
			.map(chatUser -> {
				long userIsHost = travelUserRepository.countTravelWhereUserIsHost(chatUser.getId());
				return ParticipantsInfoResponse.of(chatUser.getUser(), userIsHost);
			}).toList();

		List<ChatAlbumResponse> chatAlbumResponses = chatImageRepository.findAllByChat(chat.getId()).stream()
			.map(ChatAlbumResponse::from)
			.toList();

		return new ChatOverviewResponse(participantsInfoResponses, chatAlbumResponses);
	}

	@Transactional
	public void checkIsUserHasJoined(Long chatId, Long userId, boolean isFirstSubscribe) {
		if (!chatRepository.existsById(chatId)) {
			throw new WeGoException(CHAT_NOT_FOUND);
		}
		List<ChatUser> chatUserList = chatUserRepository.findAllChatUserByChatID(chatId);
		User sender = null;
		for (ChatUser chatUser : chatUserList) {
			if (chatUser.getUser().getId().equals(userId)) {
				sender = chatUser.getUser();
				break;
			}
		}
		if (sender == null) {
			throw new WeGoException(USER_NOT_FOUND);
		}

		if (isFirstSubscribe) {
			chatMessageUserRepository.findAllUnReadMessageByUserIdAndChatId(userId, chatId)
				.forEach(chatMessageUser -> {
					chatMessageUser.markAsRead();
					chatMessageUserRepository.flush();
					long readCount = chatMessageUserRepository.countByChatMessageAndHasReadIsTrue(
						chatMessageUser.getChatMessage());
					long unreadCount = chatMessageUser.getChat().getCurrentMemberCount() - readCount;
					messagingTemplate.convertAndSend("/sub/chat/read/" + chatId,
						new MarkAsReadSubscribe(chatMessageUser.getChat().getId(), unreadCount));
				});
		}
	}
}
