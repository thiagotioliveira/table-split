package dev.thiagooliveira.tablesplit.application.exception;

public abstract class ApplicationException extends RuntimeException {
  public ApplicationException() {
    super();
  }

  public ApplicationException(String message) {
    super(message);
  }
}
