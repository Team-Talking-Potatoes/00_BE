package potatoes.server.review.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewConfig {
	@Bean
	public GroupedOpenApi getReviewApi() {
		return GroupedOpenApi.builder()
			.group("review")
			.packagesToScan("potatoes.server.review.controller")
			.build();
	}
}
