package dev.thiagooliveira.tablesplit.domain.account;

public class PasswordValidator {
  public static void validate(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("error.password.requirements.min.char");
    }
    if (password.chars().noneMatch(Character::isUpperCase)) {
      throw new IllegalArgumentException("error.password.requirements.one.capital.letter");
    }
    if (password.chars().noneMatch(Character::isLowerCase)) {
      throw new IllegalArgumentException("error.password.requirements.one.lowercase.letter");
    }
    if (password.chars().noneMatch(Character::isDigit)) {
      throw new IllegalArgumentException("error.password.requirements.one.number");
    }
  }
}
