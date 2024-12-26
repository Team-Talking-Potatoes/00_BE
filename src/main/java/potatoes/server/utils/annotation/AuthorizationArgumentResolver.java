package potatoes.server.utils.annotation;

import static potatoes.server.error.ErrorCode.*;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import potatoes.server.error.exception.WeGoException;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Component
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtTokenUtil jwtTokenProvider;

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

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			throw new WeGoException(COOKIE_NOT_FOUND);
		}

		String accessToken = null;
		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				accessToken = cookie.getValue();
				break;
			}
		}

		if (accessToken == null) {
			throw new WeGoException(TOKEN_NOT_FOUND);
		}

		if (!jwtTokenProvider.validateToken(accessToken)) {
			throw new WeGoException(UNAUTHORIZED);
		}

		return Long.parseLong(jwtTokenProvider.getPayload(accessToken));
	}
}
