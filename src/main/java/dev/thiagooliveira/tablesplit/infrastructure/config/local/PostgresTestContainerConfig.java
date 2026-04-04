package dev.thiagooliveira.tablesplit.infrastructure.config.local;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Configuration to start a PostgreSQL container using Testcontainers when the 'postgres' profile is
 * active. This avoids the need to manually run docker-compose during local development.
 */
@Configuration
@Profile("postgres")
public class PostgresTestContainerConfig {

  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("tablesplit")
        .withUsername("postgres")
        .withPassword("password");
  }
}
