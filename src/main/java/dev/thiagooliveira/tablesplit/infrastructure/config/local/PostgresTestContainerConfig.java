package dev.thiagooliveira.tablesplit.infrastructure.config.local;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Configuration to start a PostgreSQL container using Testcontainers when the
 * 'test-container-postgres' profile is active. This avoids the need to manually run docker-compose
 * during local development.
 */
@Configuration
@Profile("test-container-postgres")
public class PostgresTestContainerConfig {

  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgreSQLContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("tablesplit")
        .withUsername("postgres")
        .withPassword("password")
        .withCreateContainerCmdModifier(
            cmd ->
                cmd.withHostConfig(
                    new HostConfig()
                        .withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))));
  }
}
