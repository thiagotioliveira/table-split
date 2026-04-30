package dev.thiagooliveira.tablesplit.application.order.model;

import java.math.BigDecimal;

public class TicketItemOptionRequest {
  private String text;
  private BigDecimal extraPrice;

  public TicketItemOptionRequest() {}

  public TicketItemOptionRequest(String text, BigDecimal extraPrice) {
    this.text = text;
    this.extraPrice = extraPrice;
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
