package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "promotions")
public class PromotionEntity {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(nullable = false)
  private String name;

  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type", nullable = false)
  private DiscountType discountType;

  @Column(name = "discount_value", nullable = false)
  private BigDecimal discountValue;

  @Column(name = "min_order_value")
  private BigDecimal minOrderValue;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(nullable = false)
  private boolean active;

  @ElementCollection(targetClass = DayOfWeek.class)
  @CollectionTable(
      name = "promotion_recurrence_days",
      joinColumns = @JoinColumn(name = "promotion_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week")
  private Set<DayOfWeek> recurrenceDays = new HashSet<>();

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "end_time")
  private LocalTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "apply_type", nullable = false)
  private ApplyType applyType;

  @ElementCollection
  @CollectionTable(
      name = "promotion_applicable_ids",
      joinColumns = @JoinColumn(name = "promotion_id"))
  @Column(name = "applicable_id")
  private Set<String> applicableIds = new HashSet<>();

  public Promotion toDomain() {
    var domain = new Promotion();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setName(this.name);
    domain.setDescription(this.description);
    domain.setDiscountType(this.discountType);
    domain.setDiscountValue(this.discountValue);
    domain.setMinOrderValue(this.minOrderValue);
    domain.setStartDate(this.startDate);
    domain.setEndDate(this.endDate);
    domain.setActive(this.active);
    domain.setApplyType(this.applyType);
    domain.setApplicableIds(new HashSet<>(this.applicableIds));
    domain.setDaysOfWeek(new HashSet<>(this.recurrenceDays));
    domain.setStartTime(this.startTime);
    domain.setEndTime(this.endTime);

    return domain;
  }

  public static PromotionEntity fromDomain(Promotion domain) {
    var entity = new PromotionEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setDiscountType(domain.getDiscountType());
    entity.setDiscountValue(domain.getDiscountValue());
    entity.setMinOrderValue(domain.getMinOrderValue());
    entity.setStartDate(domain.getStartDate());
    entity.setEndDate(domain.getEndDate());
    entity.setActive(domain.isActive());
    entity.setApplyType(domain.getApplyType());
    entity.setApplicableIds(
        new HashSet<>(
            domain.getApplicableIds() != null ? domain.getApplicableIds() : new HashSet<>()));
    entity.setRecurrenceDays(
        new HashSet<>(domain.getDaysOfWeek() != null ? domain.getDaysOfWeek() : new HashSet<>()));
    entity.setStartTime(domain.getStartTime());
    entity.setEndTime(domain.getEndTime());

    return entity;
  }

  // Getters/Setters (omitted for brevity in thinking, will implement full ones)
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

  public void setActive(boolean active) {
    this.active = active;
  }

  public Set<DayOfWeek> getRecurrenceDays() {
    return recurrenceDays;
  }

  public void setRecurrenceDays(Set<DayOfWeek> recurrenceDays) {
    this.recurrenceDays = recurrenceDays;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public ApplyType getApplyType() {
    return applyType;
  }

  public void setApplyType(ApplyType applyType) {
    this.applyType = applyType;
  }

  public Set<String> getApplicableIds() {
    return applicableIds;
  }

  public void setApplicableIds(Set<String> applicableIds) {
    this.applicableIds = applicableIds;
  }
}
