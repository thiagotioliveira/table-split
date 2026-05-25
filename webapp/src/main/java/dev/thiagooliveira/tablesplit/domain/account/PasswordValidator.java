package dev.thiagooliveira.tablesplit.domain.account;

public class PasswordValidator {
  public static void validate(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("error.password.requirements.min.char");
    }
    if (!password.matches(".*[A-Z].*")) {
      throw new IllegalArgumentException("error.password.requirements.one.capital.letter");
    }
    if (!password.matches(".*[a-z].*")) {
      throw new IllegalArgumentException("error.password.requirements.one.lowercase.letter");
    }
    if (!password.matches(".*\\d.*")) {
      throw new IllegalArgumentException("error.password.requirements.one.number");
    }
  }
}
