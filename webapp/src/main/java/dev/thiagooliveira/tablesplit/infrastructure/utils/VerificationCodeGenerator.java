package dev.thiagooliveira.tablesplit.infrastructure.utils;

import java.security.SecureRandom;
import java.util.Random;

public final class VerificationCodeGenerator {

  private static final Random random = new SecureRandom();

  private VerificationCodeGenerator() {}

  public static String generate6DigitCode() {
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }
}
