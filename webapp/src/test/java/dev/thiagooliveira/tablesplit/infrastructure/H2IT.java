package dev.thiagooliveira.tablesplit.infrastructure;

import dev.thiagooliveira.tablesplit.infrastructure.web.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Deprecated
@SpringBootTest
@ActiveProfiles({"integration-test", "h2"})
public abstract class H2IT extends BaseRegisteredSpringTest {

  @Autowired protected CustomUserDetailsService userDetailsService;

  protected AccountContext accountContext;

  @Override
  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();
    accountContext =
        (AccountContext) userDetailsService.loadUserByUsername(PROFESSIONAL_REGISTERED_EMAIL);
  }
}
