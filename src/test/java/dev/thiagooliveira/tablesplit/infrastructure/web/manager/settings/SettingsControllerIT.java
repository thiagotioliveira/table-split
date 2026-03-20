package dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import dev.thiagooliveira.tablesplit.infrastructure.web.AuthenticatedIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SettingsControllerIT extends AuthenticatedIT {

  @Autowired private RestaurantRepository restaurantRepository;

  @Test
  void shouldReturnSettingsView_whenAuthenticated() throws Exception {
    mockMvc
        .perform(get("/settings").with(user(accountContext)))
        .andExpect(status().isOk())
        .andExpect(view().name("settings"))
        .andExpect(model().attributeExists("context"))
        .andExpect(model().attributeExists("form"))
        .andExpect(model().attributeExists("module"));
  }

  @Test
  void shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
    mockMvc.perform(get("/settings")).andExpect(status().is3xxRedirection());
  }

  @Test
  void shouldUpdateSettingsAndRedirect_whenValidData() throws Exception {
    mockMvc
        .perform(
            post("/settings")
                .with(user(accountContext))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Updated Name")
                .param("slug", "updated-slug")
                .param("description", "Updated Description")
                .param("email", "updated@example.com")
                .param("currency", Currency.EUR.name())
                .param("serviceFee", "15")
                .param("averagePrice", "50-100")
                .param("hashPrimaryColor", "#000000")
                .param("hashAccentColor", "#ffffff")
                .param("cuisineType", CuisineType.BRAZILIAN.name())
                .param("tags", Tag.WIFI.name())
                .param("customerLanguages", Language.EN.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/settings"))
        .andExpect(flash().attributeExists("alert"));

    Restaurant updated =
        restaurantRepository.findById(accountContext.getRestaurant().getId()).orElseThrow();
    Assertions.assertEquals("Updated Name", updated.getName());
    Assertions.assertEquals("updated-slug", updated.getSlug());
    Assertions.assertEquals(Currency.EUR, updated.getCurrency());
    Assertions.assertEquals(15, updated.getServiceFee());
  }

  @Test
  void shouldReturnSettingsViewWithErrors_whenInvalidData() throws Exception {
    mockMvc
        .perform(
            post("/settings")
                .with(user(accountContext))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "") // Invalid: blank
                .param("slug", "invalid slug") // Invalid: space
                .param("email", "not-an-email")
                .param("address", "")
                .param("currency", "")
                .param("averagePrice", "")
                .param("hashPrimaryColor", "")
                .param("hashAccentColor", "")) // Invalid: format
        .andExpect(status().isOk())
        .andExpect(view().name("settings"))
        .andExpect(model().hasErrors())
        .andExpect(
            model()
                .attributeHasFieldErrors(
                    "form",
                    "name",
                    "slug",
                    "email",
                    "address",
                    "currency",
                    "customerLanguages",
                    "averagePrice",
                    "hashPrimaryColor",
                    "hashAccentColor"));
  }
}
