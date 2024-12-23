package potatoes.server.utils.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisStore {
	private final RedisTemplate<String, String> redisTemplate;

	public void store(String key, String value, Duration duration) {
		redisTemplate.opsForValue().set(key, value, duration);
	}

	public String find(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void remove(String value) {
		redisTemplate.delete(value);
	}
}
