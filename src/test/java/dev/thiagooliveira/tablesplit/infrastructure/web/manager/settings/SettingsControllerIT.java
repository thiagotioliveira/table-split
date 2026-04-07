package dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.thiagooliveira.tablesplit.application.account.AccountRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import dev.thiagooliveira.tablesplit.infrastructure.web.AuthenticatedIT;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class SettingsControllerIT extends AuthenticatedIT {

  @Autowired private RestaurantRepository restaurantRepository;
  @Autowired private AccountRepository accountRepository;

  @MockitoSpyBean
  private dev.thiagooliveira.tablesplit.infrastructure.listener.menu.MenuLanguageListener
      menuLanguageListener;

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
                .param("address", "Updated Address")
                .param("currency", Currency.EUR.name())
                .param("serviceFee", "15")
                .param("averagePrice", AveragePrice.PRICE_50_100.name())
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

  @Test
  void shouldFail_whenSlugAlreadyExists() throws Exception {
    // 1. Create a second account
    Account account = new Account();
    account.setId(UUID.randomUUID());
    account.setPlan(Plan.PRO);
    account.setActive(true);
    account.setCreatedAt(OffsetDateTime.now());
    accountRepository.save(account);

    // 2. Create another restaurant associated with that account
    Restaurant other = new Restaurant();
    other.setId(UUID.randomUUID());
    other.setAccountId(account.getId());
    other.setName("Other Restaurant");
    other.setSlug("already-taken");
    other.setEmail("other@example.com");
    other.setAddress("Other Address");
    other.setPhone("123456789");
    other.setCurrency(Currency.EUR);
    other.setDefaultLanguage(Language.PT);
    other.setHashPrimaryColor("#000000");
    other.setHashAccentColor("#ffffff");
    other.setServiceFee(10);
    other.setAveragePrice(AveragePrice.PRICE_20_50);
    other.setCuisineType(CuisineType.ITALIAN);
    restaurantRepository.save(other);

    // 3. Try to update our restaurant to use the same slug
    mockMvc
        .perform(
            post("/settings")
                .with(user(accountContext))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "My Restaurant")
                .param("slug", "already-taken") // Conflict!
                .param("description", "Desc")
                .param("email", "my@example.com")
                .param("address", "My Address")
                .param("currency", Currency.BRL.name())
                .param("serviceFee", "10")
                .param("averagePrice", AveragePrice.PRICE_100_150.name())
                .param("customerLanguages", Language.PT.name())
                .param("hashPrimaryColor", "#ff0000")
                .param("hashAccentColor", "#00ff00")
                .param("cuisineType", CuisineType.ITALIAN.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            redirectedUrl(
                "/register")) // ExceptionHandler redirects to /register for slug conflicts
        .andExpect(flash().attributeExists("alert"));
  }

  @Test
  void shouldRollback_whenListenerFails() throws Exception {
    // 1. Force the listener to fail
    doThrow(new RuntimeException("Simulated Listener Failure"))
        .when(menuLanguageListener)
        .onRestaurantUpdated(any());

    String originalName = accountContext.getRestaurant().getName();

    // 2. Try to update - this will throw an exception that bubbles up
    // In a real request this shows an error page, here MockMvc will receive the exception
    try {
      mockMvc.perform(
          post("/settings")
              .with(user(accountContext))
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .param("name", "New Name That Should Rollback")
              .param("slug", "new-slug-rollback")
              .param("currency", Currency.BRL.name())
              .param("serviceFee", "10")
              .param("averagePrice", AveragePrice.PRICE_100_150.name())
              .param("customerLanguages", Language.PT.name())
              .param("hashPrimaryColor", "#ff0000")
              .param("hashAccentColor", "#00ff00")
              .param("cuisineType", CuisineType.ITALIAN.name()));
    } catch (Exception e) {
      // Expected exception from the listener
    }

    // 3. Verify that the name was NOT updated in the database
    Restaurant notUpdated =
        restaurantRepository.findById(accountContext.getRestaurant().getId()).orElseThrow();
    assertEquals(originalName, notUpdated.getName(), "The update should have been rolled back!");
  }
}
