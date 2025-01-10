package potatoes.server.chat.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ChatImage {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chat_message_id")
	private ChatMessage chatMessage;

	@Column(nullable = false)
	private String imageUrl;

	public ChatImage(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void messageSent(ChatMessage chatMessage) {
		if (this.chatMessage != null) {
			this.chatMessage.getChatImages().remove(this);
		}
		this.chatMessage = chatMessage;
		chatMessage.getChatImages().add(this);
	}
}
