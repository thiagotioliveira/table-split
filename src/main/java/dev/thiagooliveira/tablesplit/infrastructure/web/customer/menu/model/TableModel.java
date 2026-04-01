package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;

public class TableModel {
  private final String id;
  private final String cod;
  private final TableStatus status;

  public TableModel(Table table) {
    this.id = table.getId().toString();
    this.cod = table.getCod();
    this.status = table.getStatus();
  }

  public String getId() {
    return id;
  }

  public String getCod() {
    return cod;
  }

  public TableStatus getStatus() {
    return status;
  }
}
