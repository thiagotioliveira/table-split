package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.*;
import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.CouponRuleType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.spec.v1.model.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PromotionsApiMapper {

  // ============================
  // Domain Mappings
  // ============================

  @Mapping(target = "restaurantId", ignore = true)
  public abstract Promotion toDomain(SavePromotionRequest request);

  public abstract PromotionResponse toResponse(Promotion promotion);

  @Mapping(target = "restaurantId", ignore = true)
  public abstract Combo toDomain(SaveComboRequest request);

  public abstract ComboResponse toResponse(Combo combo);

  @Mapping(target = "restaurantId", ignore = true)
  @Mapping(target = "usedCount", ignore = true)
  public abstract Coupon toDomain(SaveCouponRequest request);

  public abstract CouponResponse toResponse(Coupon coupon);

  // ============================
  // Command Mappings (request → command)
  // ============================

  public CreatePromotionCommand toCreatePromotionCommand(SavePromotionRequest request) {
    return new CreatePromotionCommand(
        request.getName(),
        request.getDescription(),
        mapDiscountTypeFromRequest(request.getDiscountType()),
        request.getDiscountValue(),
        request.getMinOrderValue(),
        mapFromOffsetDateTime(request.getStartDate()),
        mapFromOffsetDateTime(request.getEndDate()),
        mapDaysOfWeekFromRequest(request.getDaysOfWeek()),
        mapLocalTime(request.getStartTime()),
        mapLocalTime(request.getEndTime()),
        mapApplyType(request.getApplyType()),
        request.getApplicableIds() != null
            ? new java.util.HashSet<>(request.getApplicableIds())
            : null,
        Boolean.TRUE.equals(request.getActive()));
  }

  public UpdatePromotionCommand toUpdatePromotionCommand(SavePromotionRequest request) {
    return new UpdatePromotionCommand(
        request.getName(),
        request.getDescription(),
        mapDiscountTypeFromRequest(request.getDiscountType()),
        request.getDiscountValue(),
        request.getMinOrderValue(),
        mapFromOffsetDateTime(request.getStartDate()),
        mapFromOffsetDateTime(request.getEndDate()),
        mapDaysOfWeekFromRequest(request.getDaysOfWeek()),
        mapLocalTime(request.getStartTime()),
        mapLocalTime(request.getEndTime()),
        mapApplyType(request.getApplyType()),
        request.getApplicableIds() != null
            ? new java.util.HashSet<>(request.getApplicableIds())
            : null,
        Boolean.TRUE.equals(request.getActive()));
  }

  public CreateComboCommand toCreateComboCommand(SaveComboRequest request) {
    List<Combo.ComboItem> items =
        request.getItems() == null
            ? List.of()
            : request.getItems().stream().map(this::mapComboItem).collect(Collectors.toList());
    return new CreateComboCommand(
        request.getName(),
        request.getDescription(),
        request.getComboPrice(),
        mapFromOffsetDateTime(request.getStartDate()),
        mapFromOffsetDateTime(request.getEndDate()),
        items,
        Boolean.TRUE.equals(request.getActive()));
  }

  public UpdateComboCommand toUpdateComboCommand(SaveComboRequest request) {
    List<Combo.ComboItem> items =
        request.getItems() == null
            ? List.of()
            : request.getItems().stream().map(this::mapComboItem).collect(Collectors.toList());
    return new UpdateComboCommand(
        request.getName(),
        request.getDescription(),
        request.getComboPrice(),
        mapFromOffsetDateTime(request.getStartDate()),
        mapFromOffsetDateTime(request.getEndDate()),
        items,
        Boolean.TRUE.equals(request.getActive()));
  }

  public CreateCouponCommand toCreateCouponCommand(SaveCouponRequest request) {
    List<Coupon.CouponRule> rules =
        request.getRules() == null
            ? List.of()
            : request.getRules().stream().map(this::mapCouponRule).collect(Collectors.toList());
    return new CreateCouponCommand(
        request.getCode(),
        request.getName(),
        mapCouponDiscountType(request.getDiscountType()),
        request.getDiscountValue(),
        request.getFreeItemId(),
        request.getMinOrderValue(),
        request.getValidDate(),
        request.getUsageLimit(),
        rules,
        Boolean.TRUE.equals(request.getActive()));
  }

  public UpdateCouponCommand toUpdateCouponCommand(SaveCouponRequest request) {
    List<Coupon.CouponRule> rules =
        request.getRules() == null
            ? List.of()
            : request.getRules().stream().map(this::mapCouponRule).collect(Collectors.toList());
    return new UpdateCouponCommand(
        request.getCode(),
        request.getName(),
        mapCouponDiscountType(request.getDiscountType()),
        request.getDiscountValue(),
        request.getFreeItemId(),
        request.getMinOrderValue(),
        request.getValidDate(),
        request.getUsageLimit(),
        rules,
        Boolean.TRUE.equals(request.getActive()));
  }

  // ============================
  // Helper Methods
  // ============================

  protected OffsetDateTime mapToOffsetDateTime(LocalDateTime value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
  }

  protected LocalDateTime mapFromOffsetDateTime(OffsetDateTime value) {
    return value == null ? null : value.toLocalDateTime();
  }

  protected LocalTime mapLocalTime(String value) {
    return value == null || value.isBlank() ? null : LocalTime.parse(value);
  }

  protected Set<DayOfWeek> mapDaysOfWeekFromRequest(
      List<SavePromotionRequest.DaysOfWeekEnum> value) {
    if (value == null) return null;
    return value.stream().map(e -> DayOfWeek.valueOf(e.name())).collect(Collectors.toSet());
  }

  protected List<PromotionResponse.DaysOfWeekEnum> mapDaysOfWeekToResponse(Set<DayOfWeek> value) {
    if (value == null) return null;
    return value.stream()
        .map(e -> PromotionResponse.DaysOfWeekEnum.valueOf(e.name()))
        .collect(Collectors.toList());
  }

  protected ApplyType mapApplyType(SavePromotionRequest.ApplyTypeEnum value) {
    return value == null ? null : ApplyType.valueOf(value.name());
  }

  protected DiscountType mapDiscountTypeFromRequest(SavePromotionRequest.DiscountTypeEnum value) {
    return value == null ? null : DiscountType.valueOf(value.name());
  }

  protected DiscountType mapCouponDiscountType(SaveCouponRequest.DiscountTypeEnum value) {
    return value == null ? null : DiscountType.valueOf(value.name());
  }

  protected PromotionResponse.DiscountTypeEnum mapDiscountTypeToResponse(DiscountType value) {
    if (value == null || value == DiscountType.FREE_ITEM) return null;
    return PromotionResponse.DiscountTypeEnum.valueOf(value.name());
  }

  protected Combo.ComboItem mapComboItem(ComboItemRequest request) {
    if (request == null) return null;
    return new Combo.ComboItem(request.getItemId().toString(), request.getQuantity());
  }

  protected ComboItemResponse mapComboItemToResponse(Combo.ComboItem domain) {
    if (domain == null) return null;
    ComboItemResponse response = new ComboItemResponse();
    response.setItemId(UUID.fromString(domain.getItemId()));
    response.setQuantity(domain.getQuantity());
    return response;
  }

  protected Coupon.CouponRule mapCouponRule(CouponRuleRequest request) {
    if (request == null) return null;
    CouponRuleType type =
        request.getType() == null ? null : CouponRuleType.valueOf(request.getType().name());
    return new Coupon.CouponRule(type, request.getValue());
  }

  protected CouponRuleRequest mapCouponRuleToResponse(Coupon.CouponRule rule) {
    if (rule == null) return null;
    CouponRuleRequest response = new CouponRuleRequest();
    if (rule.type() != null) {
      response.setType(CouponRuleRequest.TypeEnum.valueOf(rule.type().name()));
    }
    response.setValue(rule.value());
    return response;
  }
}
