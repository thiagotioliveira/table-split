package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.spec.v1.api.CombosApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.spec.v1.api.CouponsApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.spec.v1.api.PromotionsApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.spec.v1.model.*;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/promotions")
public class PromotionsApiController implements PromotionsApi, CombosApi, CouponsApi {

  private final GetPromotions getPromotions;
  private final CreatePromotion createPromotion;
  private final UpdatePromotion updatePromotion;
  private final DeletePromotion deletePromotion;
  private final TogglePromotion togglePromotion;

  private final GetCombos getCombos;
  private final CreateCombo createCombo;
  private final UpdateCombo updateCombo;
  private final DeleteCombo deleteCombo;
  private final ToggleCombo toggleCombo;

  private final GetCoupons getCoupons;
  private final CreateCoupon createCoupon;
  private final UpdateCoupon updateCoupon;
  private final DeleteCoupon deleteCoupon;
  private final ToggleCoupon toggleCoupon;

  private final TransactionalContext transactionalContext;
  private final PromotionsApiMapper mapper;

  public PromotionsApiController(
      GetPromotions getPromotions,
      CreatePromotion createPromotion,
      UpdatePromotion updatePromotion,
      DeletePromotion deletePromotion,
      TogglePromotion togglePromotion,
      GetCombos getCombos,
      CreateCombo createCombo,
      UpdateCombo updateCombo,
      DeleteCombo deleteCombo,
      ToggleCombo toggleCombo,
      GetCoupons getCoupons,
      CreateCoupon createCoupon,
      UpdateCoupon updateCoupon,
      DeleteCoupon deleteCoupon,
      ToggleCoupon toggleCoupon,
      TransactionalContext transactionalContext,
      PromotionsApiMapper mapper) {
    this.getPromotions = getPromotions;
    this.createPromotion = createPromotion;
    this.updatePromotion = updatePromotion;
    this.deletePromotion = deletePromotion;
    this.togglePromotion = togglePromotion;
    this.getCombos = getCombos;
    this.createCombo = createCombo;
    this.updateCombo = updateCombo;
    this.deleteCombo = deleteCombo;
    this.toggleCombo = toggleCombo;
    this.getCoupons = getCoupons;
    this.createCoupon = createCoupon;
    this.updateCoupon = updateCoupon;
    this.deleteCoupon = deleteCoupon;
    this.toggleCoupon = toggleCoupon;
    this.transactionalContext = transactionalContext;
    this.mapper = mapper;
  }

  private UUID getRestaurantId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AccountContext context = (AccountContext) auth.getPrincipal();
    return context.getRestaurant().getId();
  }

  @Override
  public ResponseEntity<PromotionResponse> savePromotion(SavePromotionRequest request) {
    var restaurantId = getRestaurantId();
    if (request.getId() == null) {
      Promotion created =
          transactionalContext.execute(
              () ->
                  createPromotion.execute(restaurantId, mapper.toCreatePromotionCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(created));
    } else {
      transactionalContext.execute(
          () ->
              updatePromotion.execute(
                  restaurantId, request.getId(), mapper.toUpdatePromotionCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(getPromotions.findById(request.getId()).get()));
    }
  }

  @Override
  public ResponseEntity<PromotionResponse> getPromotion(UUID id) {
    return getPromotions
        .findById(id)
        .map(p -> ResponseEntity.ok(mapper.toResponse(p)))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Void> deletePromotion(UUID id) {
    transactionalContext.execute(() -> deletePromotion.execute(id));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> togglePromotion(UUID id) {
    transactionalContext.execute(() -> togglePromotion.execute(id));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<ComboResponse> saveCombo(SaveComboRequest request) {
    var restaurantId = getRestaurantId();
    if (request.getId() == null) {
      Combo created =
          transactionalContext.execute(
              () -> createCombo.execute(restaurantId, mapper.toCreateComboCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(created));
    } else {
      transactionalContext.execute(
          () ->
              updateCombo.execute(
                  restaurantId, request.getId(), mapper.toUpdateComboCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(getCombos.findById(request.getId()).get()));
    }
  }

  @Override
  public ResponseEntity<ComboResponse> getCombo(UUID id) {
    return getCombos
        .findById(id)
        .map(c -> ResponseEntity.ok(mapper.toResponse(c)))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteCombo(UUID id) {
    transactionalContext.execute(() -> deleteCombo.execute(id));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> toggleCombo(UUID id) {
    transactionalContext.execute(() -> toggleCombo.execute(id));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<CouponResponse> saveCoupon(SaveCouponRequest request) {
    var restaurantId = getRestaurantId();
    if (request.getId() == null) {
      Coupon created =
          transactionalContext.execute(
              () -> createCoupon.execute(restaurantId, mapper.toCreateCouponCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(created));
    } else {
      transactionalContext.execute(
          () ->
              updateCoupon.execute(
                  restaurantId, request.getId(), mapper.toUpdateCouponCommand(request)));
      return ResponseEntity.ok(mapper.toResponse(getCoupons.findById(request.getId()).get()));
    }
  }

  @Override
  public ResponseEntity<CouponResponse> getCoupon(UUID id) {
    return getCoupons
        .findById(id)
        .map(c -> ResponseEntity.ok(mapper.toResponse(c)))
        .orElse(ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<Void> deleteCoupon(UUID id) {
    transactionalContext.execute(() -> deleteCoupon.execute(id));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> toggleCoupon(UUID id) {
    transactionalContext.execute(() -> toggleCoupon.execute(id));
    return ResponseEntity.noContent().build();
  }
}
