package potatoes.server.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "birth_date", nullable = false)
	private Integer birthDate;

	@Column(name = "contact", nullable = false)
	private String contact;

	@Column(name = "profile_image")
	private String profileImage;

	@Builder
	public User(String email, String password, String name, String nickname, Integer birthDate, String contact) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.birthDate = birthDate;
		this.contact = contact;
	}

	public void updateProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public void resetPassword(String password) {
		this.password = password;
	}
}
