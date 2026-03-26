package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.CouponRuleType;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "coupon_rules")
public class CouponRuleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "coupon_id", nullable = false)
  private UUID couponId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CouponRuleType type;

  @Column(name = "rule_value", nullable = false)
  private String value;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getCouponId() {
    return couponId;
  }

  public void setCouponId(UUID couponId) {
    this.couponId = couponId;
  }

  public CouponRuleType getType() {
    return type;
  }

  public void setType(CouponRuleType type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
