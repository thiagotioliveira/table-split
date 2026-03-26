package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreatePromotionCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdatePromotionCommand;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class PromotionModel {
  private UUID id;
  private String name;
  private String description;
  private DiscountType discountType;
  private BigDecimal discountValue;
  private BigDecimal minOrderValue;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate endDate;

  private String recurrenceOption;
  private List<DayOfWeek> recurrenceDays;

  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  private LocalTime startTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
  private LocalTime endTime;

  private ApplyType applyType;
  private java.util.List<String> applicableIds;
  private boolean active;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public java.time.LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(java.time.LocalDate startDate) {
    this.startDate = startDate;
  }

  public java.time.LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(java.time.LocalDate endDate) {
    this.endDate = endDate;
  }

  public String getRecurrenceOption() {
    return recurrenceOption;
  }

  public void setRecurrenceOption(String recurrenceOption) {
    this.recurrenceOption = recurrenceOption;
  }

  public List<DayOfWeek> getRecurrenceDays() {
    return recurrenceDays;
  }

  public void setRecurrenceDays(List<DayOfWeek> recurrenceDays) {
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

  public java.util.List<String> getApplicableIds() {
    return applicableIds;
  }

  public void setApplicableIds(java.util.List<String> applicableIds) {
    this.applicableIds = applicableIds;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public CreatePromotionCommand toCreatePromotionCommand() {
    return new CreatePromotionCommand(
        name,
        description,
        discountType,
        discountValue,
        minOrderValue,
        startDate != null ? startDate.atStartOfDay() : null,
        endDate != null ? endDate.atTime(LocalTime.MAX) : null,
        recurrenceDays != null ? new HashSet<>(recurrenceDays) : new HashSet<>(),
        startTime,
        endTime,
        applyType,
        applicableIds != null ? new HashSet<>(applicableIds) : new HashSet<>(),
        active);
  }

  public UpdatePromotionCommand toUpdatePromotionCommand() {
    return new UpdatePromotionCommand(
        name,
        description,
        discountType,
        discountValue,
        minOrderValue,
        startDate != null ? startDate.atStartOfDay() : null,
        endDate != null ? endDate.atTime(LocalTime.MAX) : null,
        recurrenceDays != null ? new HashSet<>(recurrenceDays) : new HashSet<>(),
        startTime,
        endTime,
        applyType,
        applicableIds != null ? new HashSet<>(applicableIds) : new HashSet<>(),
        active);
  }
}
