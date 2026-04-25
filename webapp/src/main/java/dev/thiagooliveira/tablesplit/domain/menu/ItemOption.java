package dev.thiagooliveira.tablesplit.domain.menu;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemOption {
  private UUID id;
  private String text;
  private BigDecimal extraPrice;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public BigDecimal getExtraPrice() {
    return extraPrice;
  }

  public void setExtraPrice(BigDecimal extraPrice) {
    this.extraPrice = extraPrice;
  }
}
