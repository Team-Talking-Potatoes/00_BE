package potatoes.server.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import potatoes.server.config.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "company_name", nullable = false)
	private String companyName;

	@Column(name = "image")
	private String image;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Builder
	public User(String email, String password, String name, String companyName, String image) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.companyName = companyName;
		this.image = image;
	}

	public void updateUserData(String companyName, String image) {
		this.companyName = companyName;
		if (image != null) {
			this.image = image;
		}
	}
}
