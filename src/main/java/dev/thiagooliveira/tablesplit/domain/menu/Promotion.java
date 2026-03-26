package dev.thiagooliveira.tablesplit.domain.menu;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public class Promotion {
  private UUID id;
  private UUID restaurantId;
  private String name;
  private String description;
  private DiscountType discountType;
  private BigDecimal discountValue;
  private BigDecimal minOrderValue;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Recurrence recurrence;
  private ApplyType applyType;
  private UUID applicableId;
  private boolean active;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DiscountType getDiscountType() {
    return discountType;
  }

  public void setDiscountType(DiscountType discountType) {
    this.discountType = discountType;
  }

  public BigDecimal getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(BigDecimal discountValue) {
    this.discountValue = discountValue;
  }

  public BigDecimal getMinOrderValue() {
    return minOrderValue;
  }

  public void setMinOrderValue(BigDecimal minOrderValue) {
    this.minOrderValue = minOrderValue;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public Recurrence getRecurrence() {
    return recurrence;
  }

  public void setRecurrence(Recurrence recurrence) {
    this.recurrence = recurrence;
  }

  public ApplyType getApplyType() {
    return applyType;
  }

  public void setApplyType(ApplyType applyType) {
    this.applyType = applyType;
  }

  public UUID getApplicableId() {
    return applicableId;
  }

  public void setApplicableId(UUID applicableId) {
    this.applicableId = applicableId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public record Recurrence(
      RecurrenceType type, Set<DayOfWeek> daysOfWeek, LocalTime startTime, LocalTime endTime) {}
}
