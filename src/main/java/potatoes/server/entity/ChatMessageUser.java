package potatoes.server.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ChatMessageUser {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "chat_id")
	private Chat chat;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "chat_message_id")
	private ChatMessage chatMessage;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private boolean hasRead;

	@Builder
	public ChatMessageUser(Chat chat, ChatMessage chatMessage, User user) {
		this.chat = chat;
		this.chatMessage = chatMessage;
		this.user = user;
	}

	public void markAsRead() {
		hasRead = true;
	}
}
