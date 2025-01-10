package potatoes.server.utils.jwt;

import static potatoes.server.utils.error.ErrorCode.*;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import potatoes.server.utils.error.exception.WeGoException;

@Component
public class JwtTokenUtil {

	@Value("${security.jwt.token.secret-key}")
	String secretKey;
	@Value("${security.jwt.token.expire-length}")
	private Long accessTokenExpiration;

	private Key key;

	@PostConstruct
	private void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String createAccessToken(String payload) {
		return createToken(payload, accessTokenExpiration);
	}

	private String createToken(String payload, Long expiration) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(payload)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String getPayload(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		} catch (JwtException e) {
			throw new WeGoException(UNAUTHORIZED);
		}
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			throw new WeGoException(UNAUTHORIZED);
		}
	}
}
