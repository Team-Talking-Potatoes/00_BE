package potatoes.server.user.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.user.controller.UserController;

@Configuration
public class UserConfig {
	@Bean
	public GroupedOpenApi getUserApi() {
		return GroupedOpenApi.builder()
			.group("user")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(UserController.class))
			.packagesToScan("potatoes.server.user.controller")
			.build();
	}
}
