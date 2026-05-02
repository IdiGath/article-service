package de.idigath.server;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer implements QuarkusTestResourceLifecycleManager {

  private static PostgreSQLContainer<?> postgres;

  @Override
  public Map<String, String> start() {

    postgres =
        new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("article-test")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("db/init-test-user.sql");

    postgres.start();

    Map<String, String> properties = new HashMap<>();
    properties.put("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());

    return properties;
  }

  @Override
  public void stop() {
    if (Objects.nonNull(postgres)) {
      postgres.stop();
    }
  }
}
