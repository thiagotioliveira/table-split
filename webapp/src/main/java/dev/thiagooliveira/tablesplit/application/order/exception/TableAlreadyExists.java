package dev.thiagooliveira.tablesplit.application.order.exception;

public class TableAlreadyExists extends RuntimeException {
  public TableAlreadyExists(String message) {
    super(message);
  }
}
