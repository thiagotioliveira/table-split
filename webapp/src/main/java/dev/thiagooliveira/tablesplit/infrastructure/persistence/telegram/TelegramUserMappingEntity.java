package dev.thiagooliveira.tablesplit.infrastructure.persistence.telegram;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telegram_user_mappings", schema = "public")
public class TelegramUserMappingEntity {

  @Id
  @Column(name = "chat_id")
  private Long chatId;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(name = "name")
  private String name;

  @Column(name = "role")
  private String role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  // Getters and Setters
  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public UUID getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
