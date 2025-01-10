package potatoes.server.chat.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;
import potatoes.server.user.entity.User;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ChatMessage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "chat_id")
	private Chat chat;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@OneToMany(mappedBy = "chatMessage")
	private List<ChatImage> chatImages = new ArrayList<>();

	@Column(nullable = false)
	private String message;

	@Builder
	public ChatMessage(Chat chat, User sender, String message) {
		this.chat = chat;
		this.sender = sender;
		this.message = message;
	}

	public void addChatImage(ChatImage chatImage) {
		this.chatImages.add(chatImage);
		if (chatImage.getChatMessage() != this) {
			chatImage.messageSent(this);
		}
	}
}
