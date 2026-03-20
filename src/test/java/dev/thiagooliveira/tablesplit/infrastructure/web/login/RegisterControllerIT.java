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
                .param("user.firstName", "Thiago")
                .param("user.lastName", "Oliveira")
                .param("user.email", "thiago.it@example.com")
                .param("user.phone", "123456789")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Restaurante IT")
                .param("restaurant.slug", "restaurante-it")
                .param("restaurant.description", "Descricao IT")
                .param("restaurant.phone", "987654321")
                .param("restaurant.email", "contato@restaurante-it.com")
                .param("restaurant.website", "www.restaurante-it.com")
                .param("restaurant.address", "Rua IT, 123")
                .param("restaurant.currency", Currency.BRL.name())
                .param("restaurant.serviceFee", "10"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/dashboard"));

    Assertions.assertTrue(userRepository.findByEmail("thiago.it@example.com").isPresent());
  }

  @Test
  void shouldRedirectToRegisterWithError_whenUserAlreadyExists() throws Exception {
    // First registration
    shouldRegisterUserAndRedirect_whenValidData();

    // Second registration with same email
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("user.firstName", "Outro")
                .param("user.lastName", "Nome")
                .param("user.email", "thiago.it@example.com")
                .param("user.phone", "000000000")
                .param("user.password", "password123")
                .param("user.language", Language.PT.name())
                .param("restaurant.name", "Outro Restaurante")
                .param("restaurant.slug", "outro-restaurante")
                .param("restaurant.description", "Outra Descricao")
                .param("restaurant.phone", "000000000")
                .param("restaurant.email", "outro@restaurante.com")
                .param("restaurant.address", "Outra Rua, 456")
                .param("restaurant.currency", Currency.BRL.name())
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
        .andExpect(model().attributeExists("alert"))
        .andExpect(
            model()
                .attributeHasFieldErrors(
                    "form",
                    "user.firstName",
                    "user.lastName",
                    "user.email",
                    "user.password",
                    "user.language",
                    "restaurant.name",
                    "restaurant.slug",
                    "restaurant.phone",
                    "restaurant.email",
                    "restaurant.address",
                    "restaurant.currency"));
  }
}
