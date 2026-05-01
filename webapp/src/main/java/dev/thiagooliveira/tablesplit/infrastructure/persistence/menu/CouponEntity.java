package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coupons")
public class CouponEntity {

  @Id private UUID id;

  @Column(name = "restaurant_id", nullable = false)
  private UUID restaurantId;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type", nullable = false)
  private DiscountType discountType;

  @Column(name = "discount_value")
  private BigDecimal discountValue;

  @Column(name = "free_item_id")
  private UUID freeItemId;

  @Column(name = "min_order_value")
  private BigDecimal minOrderValue;

  @Column(name = "valid_date")
  private LocalDate validDate;

  @Column(name = "usage_limit")
  private Integer usageLimit;

  @Column(name = "used_count")
  private Integer usedCount = 0;

  @Column(nullable = false)
  private boolean active;

  @OneToMany(mappedBy = "couponId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CouponRuleEntity> rules = new ArrayList<>();

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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public UUID getFreeItemId() {
    return freeItemId;
  }

  public void setFreeItemId(UUID freeItemId) {
    this.freeItemId = freeItemId;
  }

  public BigDecimal getMinOrderValue() {
    return minOrderValue;
  }

  public void setMinOrderValue(BigDecimal minOrderValue) {
    this.minOrderValue = minOrderValue;
  }

  public LocalDate getValidDate() {
    return validDate;
  }

  public void setValidDate(LocalDate validDate) {
    this.validDate = validDate;
  }

  public Integer getUsageLimit() {
    return usageLimit;
  }

  public void setUsageLimit(Integer usageLimit) {
    this.usageLimit = usageLimit;
  }

  public Integer getUsedCount() {
    return usedCount;
  }

  public void setUsedCount(Integer usedCount) {
    this.usedCount = usedCount;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public List<CouponRuleEntity> getRules() {
    return rules;
  }

  public void setRules(List<CouponRuleEntity> rules) {
    this.rules = rules;
  }
}
