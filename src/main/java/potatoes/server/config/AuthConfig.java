package potatoes.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
	@Bean
	public GroupedOpenApi getAuthApi() {
		return GroupedOpenApi.builder()
			.group("auth")
			.pathsToMatch("/controller/UserController")
			.pathsToExclude("")
			.packagesToScan("potatoes.server.controller")
			.build();
	}
}
