package dev.thiagooliveira.tablesplit.infrastructure.utils;

import java.util.Random;

public final class VerificationCodeGenerator {

  private VerificationCodeGenerator() {}

  public static String generate6DigitCode() {
    Random random = new Random();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }
}
