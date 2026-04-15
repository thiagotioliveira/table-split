package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

  public Coupon toDomain() {
    var domain = new Coupon();
    domain.setId(this.id);
    domain.setRestaurantId(this.restaurantId);
    domain.setCode(this.code);
    domain.setName(this.name);
    domain.setDiscountType(this.discountType);
    domain.setDiscountValue(this.discountValue);
    domain.setFreeItemId(this.freeItemId);
    domain.setMinOrderValue(this.minOrderValue);
    domain.setValidDate(this.validDate);
    domain.setUsageLimit(this.usageLimit);
    domain.setUsedCount(this.usedCount);
    domain.setActive(this.active);
    domain.setRules(
        this.rules.stream()
            .map(r -> new Coupon.CouponRule(r.getType(), r.getValue()))
            .collect(Collectors.toList()));
    return domain;
  }

  public static CouponEntity fromDomain(Coupon domain) {
    var entity = new CouponEntity();
    entity.setId(domain.getId());
    entity.setRestaurantId(domain.getRestaurantId());
    entity.setCode(domain.getCode().toUpperCase());
    entity.setName(domain.getName());
    entity.setDiscountType(domain.getDiscountType());
    entity.setDiscountValue(domain.getDiscountValue());
    entity.setFreeItemId(domain.getFreeItemId());
    entity.setMinOrderValue(domain.getMinOrderValue());
    entity.setValidDate(domain.getValidDate());
    entity.setUsageLimit(domain.getUsageLimit());
    entity.setUsedCount(domain.getUsedCount());
    entity.setActive(domain.isActive());
    if (domain.getRules() != null) {
      entity.setRules(
          domain.getRules().stream()
              .map(
                  r -> {
                    var ruleEntity = new CouponRuleEntity();
                    ruleEntity.setCouponId(domain.getId());
                    ruleEntity.setType(r.type());
                    ruleEntity.setValue(r.value());
                    return ruleEntity;
                  })
              .collect(Collectors.toList()));
    }
    return entity;
  }

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
