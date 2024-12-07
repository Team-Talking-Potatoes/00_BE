package potatoes.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.controller.UserController;

@Configuration
public class AuthConfig {
	@Bean
	public GroupedOpenApi getAuthApi() {
		return GroupedOpenApi.builder()
			.group("auth")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(UserController.class))
			.packagesToScan("potatoes.server.controller")
			.build();
	}
}
