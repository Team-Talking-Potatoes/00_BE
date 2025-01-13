package potatoes.server.travel.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TravelConfig {
	@Bean
	public GroupedOpenApi getTravelApi() {
		return GroupedOpenApi.builder()
			.group("travel")
			.packagesToScan("potatoes.server.travel.controller")
			.build();
	}
}
