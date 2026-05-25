package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.thiagooliveira.tablesplit.domain.account.PendingRegistrationRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.H2IT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RegisterControllerIT extends H2IT {
  @Autowired private UserRepository userRepository;
  @Autowired private PendingRegistrationRepository pendingRegistrationRepository;

  @Test
  void shouldReturnRegisterView_whenGettingRegister() throws Exception {
    mockMvc
        .perform(get("/register"))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("form"));
  }

  @Test
  void shouldRegisterUserAndRedirect_whenValidData() throws Exception {
    String email = "standalone@example.com";

    // 1. Submit Registration Form
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("user.firstName", "Standalone")
                .param("user.lastName", "User")
                .param("user.email", email)
                .param("user.phone", "111111111")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Standalone Restaurant")
                .param("restaurant.slug", "standalone-restaurant")
                .param("restaurant.description", "Descricao IT")
                .param("restaurant.phone", "987654321")
                .param("restaurant.email", "contato@standalone.com")
                .param("restaurant.website", "www.standalone.com")
                .param("restaurant.address", "Rua Standalone, 123")
                .param("restaurant.currency", Currency.BRL.name())
                .param("restaurant.serviceFee", "10")
                .param("restaurant.cuisineType", CuisineType.BRAZILIAN.name())
                .param("restaurant.averagePrice", AveragePrice.PRICE_20_50.name())
                .param("restaurant.numberOfTables", "10")
                .param("plan", "PROFESSIONAL"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/register/verify?email=standalone%40example.com"));

    // 2. Assert User is NOT yet created, but Pending Registration is stored
    Assertions.assertFalse(userRepository.findByEmail(email).isPresent());
    var pendingOpt = pendingRegistrationRepository.findByEmail(email);
    Assertions.assertTrue(pendingOpt.isPresent());

    // 3. Submit correct Code to Verify Page
    String code = pendingOpt.get().getCode();
    mockMvc
        .perform(
            post("/register/verify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
                .param("code", code))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/dashboard"));

    // 4. Assert User IS now created in DB
    Assertions.assertTrue(userRepository.findByEmail(email).isPresent());
    Assertions.assertFalse(pendingRegistrationRepository.findByEmail(email).isPresent());
  }

  @Test
  void shouldRedirectToRegisterWithError_whenUserAlreadyExists() throws Exception {
    String email = "exists@example.com";
    String slug = "exists-restaurant";

    // 1. First registration submission
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("user.firstName", "First")
                .param("user.lastName", "User")
                .param("user.email", email)
                .param("user.phone", "111111111")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "First Restaurant")
                .param("restaurant.slug", slug)
                .param("restaurant.description", "Descricao")
                .param("restaurant.phone", "123")
                .param("restaurant.email", "first@rest.com")
                .param("restaurant.address", "Rua 1")
                .param("restaurant.currency", Currency.EUR.name())
                .param("restaurant.serviceFee", "10")
                .param("restaurant.cuisineType", CuisineType.BRAZILIAN.name())
                .param("restaurant.averagePrice", AveragePrice.PRICE_20_50.name())
                .param("restaurant.numberOfTables", "1")
                .param("plan", "PROFESSIONAL"))
        .andExpect(status().is3xxRedirection());

    // Verify first registration to write the user in the database
    var pendingOpt = pendingRegistrationRepository.findByEmail(email);
    Assertions.assertTrue(pendingOpt.isPresent());
    mockMvc
        .perform(
            post("/register/verify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
                .param("code", pendingOpt.get().getCode()))
        .andExpect(status().is3xxRedirection());

    // 2. Second registration submission with same email
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("user.firstName", "Second")
                .param("user.lastName", "User")
                .param("user.email", email)
                .param("user.phone", "222222222")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Second Restaurant")
                .param("restaurant.slug", "second-slug")
                .param("restaurant.description", "Descricao")
                .param("restaurant.phone", "456")
                .param("restaurant.email", "second@rest.com")
                .param("restaurant.address", "Rua 2")
                .param("restaurant.currency", Currency.EUR.name())
                .param("restaurant.serviceFee", "10")
                .param("restaurant.cuisineType", CuisineType.BRAZILIAN.name())
                .param("restaurant.averagePrice", AveragePrice.PRICE_20_50.name())
                .param("restaurant.numberOfTables", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("alert"));
  }

  @Test
  void shouldRedirectToRegisterWithError_whenRequiredFieldsAreMissing() throws Exception {
    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("alert"));
  }
}
