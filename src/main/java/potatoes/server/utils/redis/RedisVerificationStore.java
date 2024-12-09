package potatoes.server.utils.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisVerificationStore {
	private final RedisTemplate<String, String> redisTemplate;

	public void store(String email, String verificationNumber, Duration duration) {
		redisTemplate.opsForValue().set(email, verificationNumber, duration);
	}

	public String find(String email) {
		return redisTemplate.opsForValue().get(email);
	}

	public void remove(String email) {
		redisTemplate.delete(email);
	}
}
