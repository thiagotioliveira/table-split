package dev.thiagooliveira.tablesplit.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;

public abstract class BaseRegisteredIT extends BaseIT {

  protected static final String REGISTERED_EMAIL = "authenticated.it@example.com";

  @Override
  @BeforeEach
  protected void setUp() throws Exception {
    super.setUp();
    // Register a user first to have a valid context
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("plan", Plan.PROFESSIONAL.name())
                .param("user.firstName", "Thiago")
                .param("user.lastName", "Oliveira")
                .param("user.email", REGISTERED_EMAIL)
                .param("user.phone", "123456789")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Authenticated Restaurant")
                .param("restaurant.slug", "authenticated-restaurant")
                .param("restaurant.description", "Descricao Authenticated")
                .param("restaurant.phone", "987654321")
                .param("restaurant.email", "contato@authenticated-it.com")
                .param("restaurant.website", "www.authenticated-it.com")
                .param("restaurant.address", "Rua Authenticated, 123")
                .param("restaurant.currency", Currency.BRL.name())
                .param("restaurant.serviceFee", "10")
                .param("restaurant.cuisineType", CuisineType.BRAZILIAN.name())
                .param("restaurant.averagePrice", AveragePrice.PRICE_20_50.name())
                .param("restaurant.tags", RestaurantTag.WIFI.name(), RestaurantTag.PARKING.name())
                .param("restaurant.numberOfTables", "10"))
        .andExpect(status().is3xxRedirection());
  }
}
