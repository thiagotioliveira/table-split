package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"integration-test", "postgres"})
public abstract class PostgresIT extends BaseRegisteredIT {

  @Autowired protected CustomUserDetailsService userDetailsService;

  protected AccountContext accountContext;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Override
  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();
    accountContext = userDetailsService.loadUserByUsername(REGISTERED_EMAIL);
  }
}
