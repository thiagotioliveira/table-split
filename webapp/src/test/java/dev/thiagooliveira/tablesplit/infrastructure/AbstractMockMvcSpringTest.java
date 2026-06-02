package dev.thiagooliveira.tablesplit.infrastructure;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import dev.thiagooliveira.tablesplit.infrastructure.web.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@org.springframework.context.annotation.Import(
    dev.thiagooliveira.tablesplit.infrastructure.imagestorage.config.MockCloudinaryConfig.class)
public abstract class AbstractMockMvcSpringTest {
  protected MockMvc mockMvc;

  @Autowired protected WebApplicationContext context;

  @Autowired protected CustomUserDetailsService userDetailsService;

  protected AccountContext accountContext;

  @BeforeEach
  protected void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  public void authenticatedWith(String email) {
    this.accountContext = (AccountContext) userDetailsService.loadUserByUsername(email);
  }
}
