package potatoes.server.user.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
	@Bean
	public GroupedOpenApi getUserApi() {
		return GroupedOpenApi.builder()
			.group("user")
			.packagesToScan("potatoes.server.user.controller")
			.build();
	}
}
