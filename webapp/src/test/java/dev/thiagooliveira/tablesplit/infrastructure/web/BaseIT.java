package dev.thiagooliveira.tablesplit.infrastructure.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class BaseIT {

  protected MockMvc mockMvc;

  @Autowired protected WebApplicationContext context;

  @BeforeEach
  protected void setUp() throws Exception {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            // .alwaysDo(print())
            .build();
  }
}
