package dev.thiagooliveira.tablesplit.domain.order;

public class TableSessionClosedException extends RuntimeException {
  public TableSessionClosedException(String message) {
    super(message);
  }
}
