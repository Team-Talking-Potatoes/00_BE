package potatoes.server.entity;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

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

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@Column(nullable = false)
	private boolean isImage;

	@OneToMany(mappedBy = "chatMessage")
	private List<ChatImage> chatImages;

	@Column(nullable = false)
	private String message;

	@Builder
	public ChatMessage(Chat chat, User sender, boolean isImage, String message) {
		this.chat = chat;
		this.sender = sender;
		this.isImage = isImage;
		this.message = message;
	}
}
