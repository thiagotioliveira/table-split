package dev.thiagooliveira.tablesplit.infrastructure.web.exception;

public class ForbiddenException extends RuntimeException {
  public ForbiddenException(String message) {
    super(message);
  }
}
