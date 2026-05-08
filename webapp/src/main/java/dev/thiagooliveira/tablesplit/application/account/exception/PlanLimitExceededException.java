package dev.thiagooliveira.tablesplit.application.account.exception;

public class PlanLimitExceededException extends RuntimeException {
  public PlanLimitExceededException(String message) {
    super(message);
  }
}
