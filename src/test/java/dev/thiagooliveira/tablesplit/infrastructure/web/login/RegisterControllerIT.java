package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.thiagooliveira.tablesplit.application.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.BaseIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class RegisterControllerIT extends BaseIT {
  @Autowired private UserRepository userRepository;

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
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("user.firstName", "Standalone")
                .param("user.lastName", "User")
                .param("user.email", "standalone@example.com")
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
                .param("restaurant.serviceFee", "10"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/dashboard"));

    Assertions.assertTrue(userRepository.findByEmail("standalone@example.com").isPresent());
  }

  @Test
  void shouldRedirectToRegisterWithError_whenUserAlreadyExists() throws Exception {
    String email = "exists@example.com";
    String slug = "exists-restaurant";

    // First registration
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
                .param("restaurant.serviceFee", "10"))
        .andExpect(status().is3xxRedirection());

    // Second registration with same email
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
                .param("restaurant.serviceFee", "10"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/register"))
        .andExpect(flash().attributeExists("alert"));
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
