package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateCouponCommand(
    String code,
    String name,
    DiscountType discountType,
    BigDecimal discountValue,
    UUID freeItemId,
    BigDecimal minOrderValue,
    LocalDate validDate,
    Integer usageLimit,
    List<Coupon.CouponRule> rules,
    boolean active) {}
