package dev.thiagooliveira.tablesplit.application.common;

public abstract class ApplicationException extends RuntimeException {
  public ApplicationException() {
    super();
  }

  public ApplicationException(String message) {
    super(message);
  }
}
