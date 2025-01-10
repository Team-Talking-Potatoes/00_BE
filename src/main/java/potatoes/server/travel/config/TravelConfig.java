package potatoes.server.travel.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.travel.controller.TravelController;

@Configuration
public class TravelConfig {
	@Bean
	public GroupedOpenApi getTravelApi() {
		return GroupedOpenApi.builder()
			.group("travel")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(TravelController.class))
			.packagesToScan("potatoes.server.travel.controller")
			.build();
	}
}
