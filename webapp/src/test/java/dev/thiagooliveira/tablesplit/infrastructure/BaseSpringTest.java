package dev.thiagooliveira.tablesplit.infrastructure;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Deprecated
@org.springframework.context.annotation.Import(
    dev.thiagooliveira.tablesplit.infrastructure.imagestorage.config.MockCloudinaryConfig.class)
public abstract class BaseSpringTest {

  protected MockMvc mockMvc;

  @Autowired protected WebApplicationContext context;

  @BeforeEach
  protected void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }
}
