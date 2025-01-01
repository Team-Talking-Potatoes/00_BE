package potatoes.server.utils.annotation;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AuthorizationConfig implements WebMvcConfigurer {

	private final AuthorizationArgumentResolver authorizationArgumentResolver;
	private final NonLoginAuthorizationArgumentResolver nonLoginAuthorizationArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authorizationArgumentResolver);
		resolvers.add(nonLoginAuthorizationArgumentResolver);
	}
}
