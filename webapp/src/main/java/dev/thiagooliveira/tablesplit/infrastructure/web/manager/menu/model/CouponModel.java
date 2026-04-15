package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateCouponCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCouponCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRuleType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class CouponModel {
  private UUID id;
  private String code;
  private String name;
  private DiscountType discountType;
  private BigDecimal discountValue;
  private UUID freeItemId;
  private BigDecimal minOrderValue;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate validDate;

  private Integer usageLimit;
  private List<CouponRuleType> ruleTypes;
  private List<String> ruleValues;
  private boolean active;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public List<CouponRuleType> getRuleTypes() {
    return ruleTypes;
  }

  public void setRuleTypes(List<CouponRuleType> ruleTypes) {
    this.ruleTypes = ruleTypes;
  }

  public List<String> getRuleValues() {
    return ruleValues;
  }

  public void setRuleValues(List<String> ruleValues) {
    this.ruleValues = ruleValues;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  private List<Coupon.CouponRule> toCouponRules() {
    List<Coupon.CouponRule> rules = new ArrayList<>();
    if (ruleTypes != null && ruleValues != null) {
      for (int i = 0; i < ruleTypes.size(); i++) {
        rules.add(new Coupon.CouponRule(ruleTypes.get(i), ruleValues.get(i)));
      }
    }
    return rules;
  }

  public CreateCouponCommand toCreateCouponCommand() {
    return new CreateCouponCommand(
        code,
        name,
        discountType,
        discountValue,
        freeItemId,
        minOrderValue,
        validDate,
        usageLimit,
        toCouponRules(),
        active);
  }

  public UpdateCouponCommand toUpdateCouponCommand() {
    return new UpdateCouponCommand(
        code,
        name,
        discountType,
        discountValue,
        freeItemId,
        minOrderValue,
        validDate,
        usageLimit,
        toCouponRules(),
        active);
  }
}
