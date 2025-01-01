package potatoes.server.utils.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import potatoes.server.utils.jwt.JwtTokenUtil;

@Component
public class NonLoginAuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtTokenUtil jwtTokenProvider;

	public NonLoginAuthorizationArgumentResolver(JwtTokenUtil jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(NonLoginAuthorization.class);
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
			return -1L;
		}

		String accessToken = null;
		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				accessToken = cookie.getValue();
				break;
			}
		}

		if (accessToken == null) {
			return -1L;
		}

		if (!jwtTokenProvider.validateToken(accessToken)) {
			return -1L;
		}

		return Long.parseLong(jwtTokenProvider.getPayload(accessToken));
	}
}
