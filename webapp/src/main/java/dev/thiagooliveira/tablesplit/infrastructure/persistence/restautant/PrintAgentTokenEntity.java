package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "print_agent_tokens")
public class PrintAgentTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String token;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false, unique = true)
  private RestaurantEntity restaurant;

  private ZonedDateTime createdAt;
  private ZonedDateTime lastUsedAt;

  protected PrintAgentTokenEntity() {}

  public PrintAgentTokenEntity(RestaurantEntity restaurant, String token) {
    this.restaurant = restaurant;
    this.token = token;
    this.createdAt = Time.now();
  }

  public static String generateTokenValue() {
    return "pa_live_" + UUID.randomUUID().toString().replace("-", "");
  }

  public PrintAgentToken toDomain() {
    var domain = new PrintAgentToken();
    domain.setId(this.id);
    domain.setToken(this.token);
    domain.setRestaurantId(this.restaurant.getId());
    domain.setCreatedAt(this.createdAt);
    domain.setLastUsedAt(this.lastUsedAt);
    return domain;
  }

  public static PrintAgentTokenEntity fromDomain(PrintAgentToken domain) {
    var entity = new PrintAgentTokenEntity();
    entity.setId(domain.getId());
    entity.setToken(domain.getToken());
    var restaurant = new RestaurantEntity();
    restaurant.setId(domain.getRestaurantId());
    entity.setRestaurant(restaurant);
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setLastUsedAt(domain.getLastUsedAt());
    return entity;
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

  public RestaurantEntity getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(RestaurantEntity restaurant) {
    this.restaurant = restaurant;
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
