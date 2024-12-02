package potatoes.server.utils.annotation;

import static potatoes.server.error.ErrorCode.*;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import potatoes.server.error.exception.JwtAuthException;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Component
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtTokenUtil jwtTokenProvider;

	private static final int BEARER_PREFIX_LEN = 7;

	public AuthorizationArgumentResolver(JwtTokenUtil jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Authorization.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null) {
			throw new JwtAuthException(AUTHORIZATION_HEADER_NULL);
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
			throw new JwtAuthException(INVALID_TOKEN_PREFIX);
		}

		String token = authorizationHeader.substring(BEARER_PREFIX_LEN);
		return Long.parseLong(jwtTokenProvider.getPayload(token));
	}
}
