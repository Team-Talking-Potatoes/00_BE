package potatoes.server.chat.service;

import static java.util.Comparator.*;
import static potatoes.server.constant.AlarmStatus.*;
import static potatoes.server.error.ErrorCode.*;
import static potatoes.server.utils.time.DateTimeUtils.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.chat.entity.Chat;
import potatoes.server.chat.entity.ChatImage;
import potatoes.server.chat.entity.ChatMessage;
import potatoes.server.chat.entity.ChatMessageUser;
import potatoes.server.chat.entity.ChatUser;
import potatoes.server.chat.repository.ChatImageRepository;
import potatoes.server.chat.repository.ChatMessageRepository;
import potatoes.server.chat.repository.ChatMessageUserRepository;
import potatoes.server.chat.repository.ChatRepository;
import potatoes.server.chat.repository.ChatUserRepository;
import potatoes.server.constant.ChatSortType;
import potatoes.server.dto.AlarmSubscribe;
import potatoes.server.dto.ChatAlbumResponse;
import potatoes.server.dto.ChatOverviewResponse;
import potatoes.server.dto.ChatSummaryResponse;
import potatoes.server.dto.MarkAsReadSubscribe;
import potatoes.server.dto.MessagePublish;
import potatoes.server.dto.MessageSubscribe;
import potatoes.server.dto.ParticipantsInfoResponse;
import potatoes.server.dto.RecentChatResponse;
import potatoes.server.entity.TravelUser;
import potatoes.server.entity.User;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.repository.TravelUserRepository;
import potatoes.server.utils.s3.S3UtilsProvider;
import potatoes.server.utils.stomp.StompUserPrincipal;

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
		Chat chat = chatRepository.findChatFetchJoinTravel(chatId).orElseThrow(
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

		List<String> chatImages = new ArrayList<>();
		for (String imageUrl : message.images()) {
			Optional<ChatImage> optionalChatImage = chatImageRepository.findByImageUrl(imageUrl);
			if (optionalChatImage.isPresent()) {
				ChatImage chatImage = optionalChatImage.get();
				chatImage.messageSent(chatMessage);
				chatImages.add(chatImage.getImageUrl());
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

		long userIsHost = travelUserRepository.countTravelWhereUserIsHost(sender.getId());
		AlarmSubscribe alarmSubscribe = new AlarmSubscribe(
			chat.getId(),
			chatUserList.size(),
			getYearMonthDay(chatMessage.getCreatedAt()),
			MESSAGE,
			ParticipantsInfoResponse.of(sender, userIsHost));

		travelUserRepository.findAllByTravel(chat.getTravel())
			.forEach(travelUser -> messagingTemplate.convertAndSend("/sub/alarm/" + travelUser.getUser().getId(),
				alarmSubscribe));

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

		TravelUser joinedUser = travelUserRepository.findByTravelAndUserJoinFetchUser(chat.getTravel().getId(), userId)
			.orElseThrow(
				() -> new WeGoException(UNABLE_TO_JOIN_CHAT)
			);

		if (chatUserRepository.existsByUserIdAndChatId(chatId, userId)) {
			throw new WeGoException(ALREADY_JOINED_CHAT);
		}

		ChatUser chatUser = ChatUser.builder()
			.user(joinedUser.getUser())
			.chat(chat)
			.build();
		chat.newMemberJoined();
		chatUserRepository.save(chatUser);

		long userIsHost = travelUserRepository.countTravelWhereUserIsHost(chatUser.getId());
		AlarmSubscribe alarmSubscribe = new AlarmSubscribe(
			chat.getId(),
			chat.getCurrentMemberCount(),
			getYearMonthDayTime(Instant.now()),
			JOIN,
			ParticipantsInfoResponse.of(joinedUser.getUser(), userIsHost)
		);

		travelUserRepository.findAllByTravel(chat.getTravel())
			.forEach(travelUser -> messagingTemplate.convertAndSend("/sub/alarm/" + travelUser.getUser().getId(),
				alarmSubscribe));
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
					chatMessage.getCreatedAt() == null ? getYearMonthDayTime(Instant.now()) :
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

				ChatMessage chatMessage = chatMessageRepository.findLatestMessageByChatId(chat.getId())
					.orElseGet(() -> new ChatMessage(chat, null, ""));

				return ChatSummaryResponse.of(
					chat,
					false,
					0,
					chatMessage.getCreatedAt() == null ? getYearMonthDayTime(Instant.now()) :
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
		Chat chat = chatRepository.findById(chatId)
			.orElseThrow(() -> new WeGoException(CHAT_NOT_FOUND));

		User sender = chatUserRepository.findByChatIdAndUserId(chatId, userId)
			.map(ChatUser::getUser)
			.orElseThrow(() -> new WeGoException(USER_NOT_FOUND));

		Sort sort = Sort.by(
			Sort.Order.desc("id"),
			Sort.Order.desc("createdAt")
		);

		Pageable pageable = PageRequest.of(0, size, sort);
		List<ChatMessage> chatMessages = chatMessageRepository.findDistinctByChatIdAndIdLessThanOrderByIdDesc(
			chatId,
			latestChatId == 0L ? Long.MAX_VALUE : latestChatId,
			pageable
		);

		List<MessageSubscribe> messageSubscribes = chatMessages.stream()
			.map(chatMessage -> {
				List<String> images = chatImageRepository.findAllByChatMessageId(chatMessage.getId()).stream()
					.map(ChatImage::getImageUrl).toList();
				long unreadCount = chatMessageUserRepository.countUnreadByMessageId(chatMessage.getId());
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

		List<ChatAlbumResponse> chatAlbumResponses = chatMessageRepository.findWhereImagesIsNotNull(chatId).stream()
			.map(ChatAlbumResponse::from)
			.toList();

		return new ChatOverviewResponse(participantsInfoResponses, chatAlbumResponses);
	}

	@Transactional
	public void readAllUnReadChatMessages(Long chatId, Long userId) {
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

	@Transactional
	public void leaveChat(Long userId, Long chatId) {
		Chat chat = chatRepository.findByIdFetchJoinTravel(chatId).orElseThrow(
			() -> new WeGoException(CHAT_NOT_FOUND)
		);

		ChatUser deleteRequestUser = chatUserRepository.findByChatIdAndUserId(chatId, userId).orElseThrow(
			() -> new WeGoException(HAS_NOT_JOINED_CHAT)
		);
		chatUserRepository.delete(deleteRequestUser);
		chat.memberLeaved();

		long userIsHost = travelUserRepository.countTravelWhereUserIsHost(deleteRequestUser.getId());
		AlarmSubscribe alarmSubscribe = new AlarmSubscribe(
			chat.getId(),
			chat.getCurrentMemberCount(),
			getYearMonthDayTime(Instant.now()),
			LEAVE,
			ParticipantsInfoResponse.of(deleteRequestUser.getUser(), userIsHost)
		);

		travelUserRepository.findAllByTravel(chat.getTravel())
			.forEach(travelUser -> messagingTemplate.convertAndSend("/sub/alarm/" + travelUser.getUser().getId(),
				alarmSubscribe));
	}
}
