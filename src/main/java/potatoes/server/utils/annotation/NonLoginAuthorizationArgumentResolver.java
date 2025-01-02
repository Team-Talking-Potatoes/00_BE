package potatoes.server.utils.annotation;

import java.util.Arrays;
import java.util.Optional;

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
	public Optional<Long> resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

		return Optional.ofNullable(request.getCookies())
			.flatMap(this::findAccessTokenCookie)
			.filter(jwtTokenProvider::validateToken)
			.map(jwtTokenProvider::getPayload)
			.map(Long::parseLong);
	}

	private Optional<String> findAccessTokenCookie(Cookie[] cookies) {
		return Arrays.stream(cookies)
			.filter(cookie -> "accessToken".equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst();
	}
}
