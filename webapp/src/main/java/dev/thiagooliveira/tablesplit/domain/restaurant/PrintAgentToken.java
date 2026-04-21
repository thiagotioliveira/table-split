package dev.thiagooliveira.tablesplit.domain.restaurant;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PrintAgentToken {
  private UUID id;
  private String token;
  private UUID restaurantId;
  private ZonedDateTime createdAt;
  private ZonedDateTime lastUsedAt;

  public PrintAgentToken() {}

  public PrintAgentToken(UUID restaurantId, String token) {
    this.restaurantId = restaurantId;
    this.token = token;
    this.createdAt = Time.now();
  }

  public static String generateTokenValue() {
    return "pa_live_" + UUID.randomUUID().toString().replace("-", "");
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ZonedDateTime getLastUsedAt() {
    return lastUsedAt;
  }

  public void setLastUsedAt(ZonedDateTime lastUsedAt) {
    this.lastUsedAt = lastUsedAt;
  }
}
