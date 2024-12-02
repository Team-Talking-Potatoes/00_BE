package potatoes.server.utils.crypto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public String encrypt(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	public boolean matches(String rawPassword, String encrpytedPassword) {
		return encoder.matches(rawPassword, encrpytedPassword);
	}
}
