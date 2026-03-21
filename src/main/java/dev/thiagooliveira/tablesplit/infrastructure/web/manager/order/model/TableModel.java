package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.math.BigDecimal;
import java.util.UUID;

public class TableModel {
  private final UUID id;
  private final String cod;
  private final TableStatus status;
  private final BigDecimal total;

  public TableModel(UUID id, String cod, TableStatus status, BigDecimal total) {
    this.id = id;
    this.cod = cod;
    this.status = status;
    this.total = total;
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

  public BigDecimal getTotal() {
    return total;
  }
}
