package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OpenTableForm {

  @NotBlank
  @Size(max = 50)
  private String tableCod;

  public String getTableCod() {
    return tableCod;
  }

  public void setTableCod(String tableCod) {
    this.tableCod = tableCod;
  }
}
