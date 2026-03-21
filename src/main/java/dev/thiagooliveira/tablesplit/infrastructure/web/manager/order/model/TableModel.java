package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.math.BigDecimal;
import java.util.UUID;

public class TableModel {
  private final UUID id;
  private final String cod;
  private final TableStatus status;
  private final BigDecimal balance;

  public TableModel(UUID id, String cod, TableStatus status, BigDecimal balance) {
    this.id = id;
    this.cod = cod;
    this.status = status;
    this.balance = balance;
  }

  public UUID getId() {
    return id;
  }

  public String getCod() {
    return cod;
  }

  public TableStatus getStatus() {
    return status;
  }

  public BigDecimal getBalance() {
    return balance;
  }
}
