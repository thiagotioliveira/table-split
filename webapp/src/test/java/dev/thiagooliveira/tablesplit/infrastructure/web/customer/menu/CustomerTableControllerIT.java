package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.infrastructure.AbstractInitDatabaseStringTest;
import dev.thiagooliveira.tablesplit.infrastructure.IntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@IntegrationTest
class CustomerTableControllerIT extends AbstractInitDatabaseStringTest {

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
  }

  @Test
  void shouldAllowAccess_whenPlanIsProfessional() throws Exception {
    mockMvc
        .perform(get("/@" + professionalAccount.slug() + "/table/01"))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/@" + professionalAccount.slug() + "/table/01/menu"))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/v1/customer/" + professionalAccount.slug() + "/table/01/menu/data"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnNotFound_whenPlanIsStarter() throws Exception {
    mockMvc
        .perform(get("/@" + starterAccount.slug() + "/table/01"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/@" + starterAccount.slug() + "/menu"));

    mockMvc
        .perform(
            post("/api/v1/customer/" + starterAccount.slug() + "/table/01/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"customerId\": \"" + UUID.randomUUID() + "\", \"customerName\": \"John\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFound_whenRestaurantDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/@non-existent-" + UUID.randomUUID() + "/table/01"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFound_whenTableDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/@" + professionalAccount.slug() + "/table/99"))
        .andExpect(status().isNotFound());
  }
}
