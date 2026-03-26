package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatePromotionCommand(
    String name,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minOrderValue,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Promotion.Recurrence recurrence,
    ApplyType applyType,
    UUID applicableId,
    boolean active) {}
