package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateTableForm {

  @NotBlank
  @Pattern(regexp = "^[0-9]{2}$", message = "{validation.table.cod.size}")
  private String cod;

  public String getCod() {
    return cod;
  }

  public void setCod(String cod) {
    this.cod = cod;
  }
}
