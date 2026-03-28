package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.ComboModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.CouponModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionCategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionItemModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionModel;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    Language language = context.getUser().getLanguage();
    List<Language> languages = List.of(language);

    List<Combo> combos = getCombos.listByRestaurantId(restaurantId);
    List<Item> allItems = getItem.execute(restaurantId, languages);

    Map<String, PromotionItemModel> itemMap =
        allItems.stream()
            .map(item -> PromotionItemModel.from(item, language))
            .collect(Collectors.toMap(PromotionItemModel::id, i -> i));

    Map<UUID, BigDecimal> comboOriginalPrices = new HashMap<>();
    for (Combo combo : combos) {
      BigDecimal originalPrice = BigDecimal.ZERO;
      for (Combo.ComboItem ci : combo.getItems()) {
        PromotionItemModel item = itemMap.get(ci.getItemId());
        if (item != null && item.price() != null) {
          originalPrice =
              originalPrice.add(item.price().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
      }
      comboOriginalPrices.put(combo.getId(), originalPrice);
    }

    List<PromotionCategoryModel> promoCategories =
        getCategory.execute(restaurantId, languages).stream()
            .map(cat -> PromotionCategoryModel.from(cat, language))
            .toList();

    Map<String, PromotionCategoryModel> categoriesMap =
        promoCategories.stream().collect(Collectors.toMap(c -> c.id().toString(), c -> c));

    model.addAttribute("promotions", getPromotions.listByRestaurantId(restaurantId));
    model.addAttribute("combos", combos);
    model.addAttribute("comboOriginalPrices", comboOriginalPrices);
    model.addAttribute("coupons", getCoupons.listByRestaurantId(restaurantId));
    model.addAttribute("categories", promoCategories);
    model.addAttribute("categoriesMap", categoriesMap);
    model.addAttribute("items", itemMap.values());
    model.addAttribute("itemMap", itemMap);
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());

    return "promotions";
  }

  @PostMapping("/promotion")
  public String savePromotion(
      Authentication auth,
      @ModelAttribute PromotionModel promotionModel,
      RedirectAttributes redirectAttributes) {
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
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.promotion.saved"));
    return "redirect:/promotions?tab=promotions";
  }

  @GetMapping("/promotion/{id}")
  @ResponseBody
  public org.springframework.http.ResponseEntity<Promotion> getPromotion(@PathVariable UUID id) {
    return getPromotions
        .findById(id)
        .map(org.springframework.http.ResponseEntity::ok)
        .orElse(org.springframework.http.ResponseEntity.notFound().build());
  }

  @PostMapping("/promotion/toggle/{id}")
  @ResponseBody
  public void togglePromotion(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> togglePromotion.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.promotion.toggled"));
  }

  @PostMapping("/promotion/delete/{id}")
  public String deletePromotion(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> deletePromotion.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.promotion.deleted"));
    return "redirect:/promotions";
  }

  @GetMapping("/combo/{id}")
  @ResponseBody
  public org.springframework.http.ResponseEntity<Combo> getCombo(@PathVariable UUID id) {
    return getCombos
        .findById(id)
        .map(org.springframework.http.ResponseEntity::ok)
        .orElse(org.springframework.http.ResponseEntity.notFound().build());
  }

  @PostMapping("/combo")
  public String saveCombo(
      Authentication auth,
      @ModelAttribute ComboModel comboModel,
      RedirectAttributes redirectAttributes) {
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
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.combo.saved"));
    return "redirect:/promotions?tab=combos";
  }

  @PostMapping("/combo/toggle/{id}")
  @ResponseBody
  public void toggleCombo(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> toggleCombo.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.combo.toggled"));
  }

  @PostMapping("/combo/delete/{id}")
  public String deleteCombo(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> deleteCombo.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.combo.deleted"));
    return "redirect:/promotions";
  }

  @GetMapping("/coupon/{id}")
  @ResponseBody
  public org.springframework.http.ResponseEntity<Coupon> getCoupon(@PathVariable UUID id) {
    return getCoupons
        .findById(id)
        .map(org.springframework.http.ResponseEntity::ok)
        .orElse(org.springframework.http.ResponseEntity.notFound().build());
  }

  @PostMapping("/coupon")
  public String saveCoupon(
      Authentication auth,
      @ModelAttribute CouponModel couponModel,
      RedirectAttributes redirectAttributes) {
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
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.coupon.saved"));
    return "redirect:/promotions?tab=coupons";
  }

  @PostMapping("/coupon/toggle/{id}")
  @ResponseBody
  public void toggleCoupon(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> toggleCoupon.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.coupon.toggled"));
  }

  @PostMapping("/coupon/delete/{id}")
  public String deleteCoupon(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    transactionalContext.execute(() -> deleteCoupon.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.coupon.deleted"));
    return "redirect:/promotions";
  }
}
