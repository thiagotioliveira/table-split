package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

  @Test
  void shouldValidateCorrectPassword() {
    assertDoesNotThrow(() -> PasswordValidator.validate("Aa1bbbbb"));
  }

  @Test
  void shouldThrowExceptionWhenPasswordTooShort() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("Aa1"));
    assertEquals("error.password.requirements.min.char", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNoCapitalLetter() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("aa1bbbbb"));
    assertEquals("error.password.requirements.one.capital.letter", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNoLowercaseLetter() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("AA1BBBBB"));
    assertEquals("error.password.requirements.one.lowercase.letter", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNoNumber() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> PasswordValidator.validate("Aabbbbbb"));
    assertEquals("error.password.requirements.one.number", ex.getMessage());
  }
}
