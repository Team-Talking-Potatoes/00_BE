package potatoes.server.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatheringConfig {
	@Bean
	public GroupedOpenApi getGatheringApi() {
		return GroupedOpenApi.builder()
			.group("gathering")
			.pathsToMatch("/controller/GatheringController")
			.pathsToExclude("")
			.packagesToScan("potatoes.server.controller")
			.build();
	}
}
