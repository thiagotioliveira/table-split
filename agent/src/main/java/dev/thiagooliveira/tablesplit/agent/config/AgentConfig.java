package dev.thiagooliveira.tablesplit.agent.config;

import org.springframework.stereotype.Component;

@Component
public class AgentConfig {
  private String restaurantId;
  private String restaurantName;
  private String token;
  private String printer;
  private String rabbitHost;
  private String rabbitUsername;
  private String rabbitPassword;
  private String queueName;
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

  public String getRabbitHost() {
    return rabbitHost;
  }

  public void setRabbitHost(String rabbitHost) {
    this.rabbitHost = rabbitHost;
  }

  public String getRabbitUsername() {
    return rabbitUsername;
  }

  public void setRabbitUsername(String rabbitUsername) {
    this.rabbitUsername = rabbitUsername;
  }

  public String getRabbitPassword() {
    return rabbitPassword;
  }

  public void setRabbitPassword(String rabbitPassword) {
    this.rabbitPassword = rabbitPassword;
  }

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  public String getRoutingKey() {
    return restaurantId != null ? "restaurant." + restaurantId + ".orders" : "restaurant.*.orders";
  }
}
