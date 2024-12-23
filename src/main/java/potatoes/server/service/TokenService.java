package potatoes.server.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import potatoes.server.dto.TokenInfo;
import potatoes.server.error.exception.Unauthorized;
import potatoes.server.utils.jwt.JwtTokenUtil;
import potatoes.server.utils.redis.RedisStore;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisStore redisStore;
	@Value("${security.jwt.token.refresh-token-expire}")
	private Duration REFRESH_TOKEN_EXPIRATION;
	private static final String REFRESH_TOKEN_PREFIX = "RT:";

	@Transactional
	public TokenInfo createTokenSet(String userId) {
		String accessToken = jwtTokenUtil.createAccessToken(userId);
		String refreshToken = jwtTokenUtil.createRefreshToken(userId);

		redisStore.store(REFRESH_TOKEN_PREFIX + userId, refreshToken, REFRESH_TOKEN_EXPIRATION);

		return new TokenInfo(accessToken, refreshToken);
	}

	@Transactional
	public String refreshAccessToken(String refreshToken) {
		System.out.println("refreshToken =\n " + refreshToken);
		if (!jwtTokenUtil.validateToken(refreshToken)) {
			throw new Unauthorized();
		}

		String userId = jwtTokenUtil.getPayload(refreshToken);
		System.out.println("userId = \n" + userId);
		String storedRefreshToken = redisStore.find(REFRESH_TOKEN_PREFIX + userId);
		System.out.println("storedRefreshToken = \n" + storedRefreshToken);
		if (storedRefreshToken == null) {
			throw new Unauthorized();
		}

		if (!storedRefreshToken.equals(refreshToken)) {
			System.out.println("storedRefreshToken =\n " + storedRefreshToken);
			System.out.println("refreshToken = " + refreshToken);
			throw new Unauthorized();
		}

		return jwtTokenUtil.createAccessToken(userId);
	}

	@Transactional
	public void logout(String userId) {
		redisStore.remove(REFRESH_TOKEN_PREFIX + userId);
	}
}
