package de.idigath.articleservice.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Post-processor to integrate environment variables from the .env file into the
 * Spring Boot
 * configuration. Used to substitute values from .env into placeholders in
 * application.yml.
 *
 * <p>
 * The .env file is ignored by Git and contains sensitive information (e.g.,
 * passwords, API keys)
 * that should not be committed to the repository. This post-processor loads
 * variables from .env and
 * adds them to the Spring environment as a {@link MapPropertySource}.
 *
 * <p>
 * Registration of the post-processor is done via the spring.factories file.
 *
 * @see org.springframework.boot.env.EnvironmentPostProcessor
 * @see io.github.cdimascio.dotenv.Dotenv
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> env = Dotenv.load().entries().stream()
                .collect(Collectors.toMap(DotenvEntry::getKey, DotenvEntry::getValue));
        environment.getPropertySources().addLast(new MapPropertySource("dotenvProperties", env));
    }
}
