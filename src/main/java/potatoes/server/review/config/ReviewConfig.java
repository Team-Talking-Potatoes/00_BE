package potatoes.server.review.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import potatoes.server.review.controller.ReviewController;

@Configuration
public class ReviewConfig {
	@Bean
	public GroupedOpenApi getReviewApi() {
		return GroupedOpenApi.builder()
			.group("review")
			.addOpenApiMethodFilter(method ->
				method.getDeclaringClass().equals(ReviewController.class))
			.packagesToScan("potatoes.server.review.controller")
			.build();
	}
}
