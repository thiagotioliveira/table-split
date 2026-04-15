package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record UpdatePromotionCommand(
    String name,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minOrderValue,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Set<DayOfWeek> daysOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    ApplyType applyType,
    java.util.Set<String> applicableIds,
    boolean active) {}
