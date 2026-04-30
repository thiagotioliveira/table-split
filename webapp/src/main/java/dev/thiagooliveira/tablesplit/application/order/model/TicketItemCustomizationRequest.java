package dev.thiagooliveira.tablesplit.application.order.model;

import java.util.List;

public class TicketItemCustomizationRequest {
  private String title;
  private List<TicketItemOptionRequest> options;

  public TicketItemCustomizationRequest() {}

  public TicketItemCustomizationRequest(String title, List<TicketItemOptionRequest> options) {
    this.title = title;
    this.options = options;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<TicketItemOptionRequest> getOptions() {
    return options;
  }

  public void setOptions(List<TicketItemOptionRequest> options) {
    this.options = options;
  }
}
