package dev.thiagooliveira.tablesplit.domain.menu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Coupon {
  private UUID id;
  private UUID restaurantId;
  private String code;
  private String name;
  private DiscountType discountType;
  private BigDecimal discountValue;
  private UUID freeItemId;
  private BigDecimal minOrderValue;
  private LocalDate validDate;
  private Integer usageLimit;
  private Integer usedCount;
  private List<CouponRule> rules;
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

  public List<CouponRule> getRules() {
    return rules;
  }

  public void setRules(List<CouponRule> rules) {
    this.rules = rules;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public record CouponRule(CouponRuleType type, String value) {}
}
