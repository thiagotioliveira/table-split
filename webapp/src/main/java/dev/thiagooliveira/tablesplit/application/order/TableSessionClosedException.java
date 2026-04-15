package dev.thiagooliveira.tablesplit.application.order;

public class TableSessionClosedException extends RuntimeException {
  public TableSessionClosedException(String message) {
    super(message);
  }
}
