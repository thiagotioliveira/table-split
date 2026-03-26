package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.ComboModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.CouponModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionModel;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/promotions")
@ManagerModule(Module.PROMOTIONS)
public class PromotionsController {

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

  private final GetCategory getCategory;
  private final GetItem getItem;
  private final TransactionalContext transactionalContext;

  public PromotionsController(
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
      GetCategory getCategory,
      GetItem getItem,
      TransactionalContext transactionalContext) {
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
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.transactionalContext = transactionalContext;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    UUID restaurantId = context.getRestaurant().getId();
    List<Language> languages = List.of(context.getUser().getLanguage());

    List<Combo> combos = getCombos.listByRestaurantId(restaurantId);
    List<Item> allItems = getItem.execute(restaurantId, languages);
    Map<UUID, Item> itemMap = allItems.stream().collect(Collectors.toMap(Item::getId, i -> i));

    Map<UUID, BigDecimal> comboOriginalPrices = new HashMap<>();
    for (Combo combo : combos) {
      BigDecimal originalPrice = BigDecimal.ZERO;
      for (Combo.ComboItem ci : combo.getItems()) {
        Item item = itemMap.get(ci.itemId());
        if (item != null && item.getPrice() != null) {
          originalPrice =
              originalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(ci.quantity())));
        }
      }
      comboOriginalPrices.put(combo.getId(), originalPrice);
    }

    model.addAttribute("promotions", getPromotions.listByRestaurantId(restaurantId));
    model.addAttribute("combos", combos);
    model.addAttribute("comboOriginalPrices", comboOriginalPrices);
    model.addAttribute("coupons", getCoupons.listByRestaurantId(restaurantId));
    model.addAttribute("categories", getCategory.execute(restaurantId, languages));
    model.addAttribute("items", allItems);
    model.addAttribute("itemMap", itemMap);

    return "promotions";
  }

  @PostMapping("/promotion")
  public String savePromotion(Authentication auth, @ModelAttribute PromotionModel promotionModel) {
    AccountContext context = (AccountContext) auth.getPrincipal();

    if (promotionModel.getId() == null) {
      transactionalContext.execute(
          () ->
              createPromotion.execute(
                  context.getRestaurant().getId(), promotionModel.toCreatePromotionCommand()));
    } else {
      transactionalContext.execute(
          () ->
              updatePromotion.execute(
                  context.getRestaurant().getId(),
                  promotionModel.getId(),
                  promotionModel.toUpdatePromotionCommand()));
    }
    return "redirect:/promotions";
  }

  @GetMapping("/promotion/{id}")
  @ResponseBody
  public Promotion getPromotion(@PathVariable UUID id) {
    return getPromotions.findById(id).orElse(null);
  }

  @PostMapping("/promotion/toggle/{id}")
  @ResponseBody
  public void togglePromotion(@PathVariable UUID id) {
    transactionalContext.execute(() -> togglePromotion.execute(id));
  }

  @PostMapping("/promotion/delete/{id}")
  public String deletePromotion(@PathVariable UUID id) {
    transactionalContext.execute(() -> deletePromotion.execute(id));
    return "redirect:/promotions";
  }

  @GetMapping("/combo/{id}")
  @ResponseBody
  public Combo getCombo(@PathVariable UUID id) {
    return getCombos.findById(id).orElse(null);
  }

  @PostMapping("/combo")
  public String saveCombo(Authentication auth, @ModelAttribute ComboModel comboModel) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    if (comboModel.getId() == null) {
      transactionalContext.execute(
          () ->
              createCombo.execute(
                  context.getRestaurant().getId(), comboModel.toCreateComboCommand()));
    } else {
      transactionalContext.execute(
          () ->
              updateCombo.execute(
                  context.getRestaurant().getId(),
                  comboModel.getId(),
                  comboModel.toUpdateComboCommand()));
    }
    return "redirect:/promotions";
  }

  @PostMapping("/combo/toggle/{id}")
  @ResponseBody
  public void toggleCombo(@PathVariable UUID id) {
    transactionalContext.execute(() -> toggleCombo.execute(id));
  }

  @PostMapping("/combo/delete/{id}")
  public String deleteCombo(@PathVariable UUID id) {
    transactionalContext.execute(() -> deleteCombo.execute(id));
    return "redirect:/promotions";
  }

  @GetMapping("/coupon/{id}")
  @ResponseBody
  public Coupon getCoupon(@PathVariable UUID id) {
    return getCoupons.findById(id).orElse(null);
  }

  @PostMapping("/coupon")
  public String saveCoupon(Authentication auth, @ModelAttribute CouponModel couponModel) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    if (couponModel.getId() == null) {
      transactionalContext.execute(
          () ->
              createCoupon.execute(
                  context.getRestaurant().getId(), couponModel.toCreateCouponCommand()));
    } else {
      transactionalContext.execute(
          () ->
              updateCoupon.execute(
                  context.getRestaurant().getId(),
                  couponModel.getId(),
                  couponModel.toUpdateCouponCommand()));
    }
    return "redirect:/promotions";
  }

  @PostMapping("/coupon/toggle/{id}")
  @ResponseBody
  public void toggleCoupon(@PathVariable UUID id) {
    transactionalContext.execute(() -> toggleCoupon.execute(id));
  }

  @PostMapping("/coupon/delete/{id}")
  public String deleteCoupon(@PathVariable UUID id) {
    transactionalContext.execute(() -> deleteCoupon.execute(id));
    return "redirect:/promotions";
  }
}
