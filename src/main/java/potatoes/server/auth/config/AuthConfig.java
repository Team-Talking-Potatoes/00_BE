package potatoes.server.auth.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.auth.controller.AuthController;

@Configuration
public class AuthConfig {
	@Bean
	public GroupedOpenApi getAuthApi() {
		return GroupedOpenApi.builder()
			.group("auth")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(AuthController.class))
			.packagesToScan("potatoes.server.auth.controller")
			.build();
	}
}
