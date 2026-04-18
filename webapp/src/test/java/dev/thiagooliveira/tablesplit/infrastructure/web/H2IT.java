package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"integration-test", "h2"})
public abstract class H2IT extends BaseRegisteredIT {

  @Autowired protected CustomUserDetailsService userDetailsService;

  protected AccountContext accountContext;

  @Override
  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();
    accountContext = userDetailsService.loadUserByUsername(REGISTERED_EMAIL);
  }
}
