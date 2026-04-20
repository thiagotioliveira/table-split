package dev.thiagooliveira.tablesplit.agent.config;

import org.springframework.stereotype.Component;

@Component
public class AgentConfig {
  private String restaurantId;
  private String restaurantName;
  private String token;
  private String printer;
  private boolean connected = false;

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getRestaurantName() {
    return restaurantName;
  }

  public void setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getPrinter() {
    return printer;
  }

  public void setPrinter(String printer) {
    this.printer = printer;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public String getRoutingKey() {
    return restaurantId != null ? "restaurant." + restaurantId + ".orders" : "restaurant.*.orders";
  }
}
