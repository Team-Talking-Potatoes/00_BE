package potatoes.server.example;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleConfig {
	@Bean
	public GroupedOpenApi getExampleApi() {
		return GroupedOpenApi.builder()
			.group("example")
			.pathsToMatch("/example/ExampleController")
			.pathsToExclude("")
			.packagesToScan("potatoes.server.example")
			.build();
	}
}
