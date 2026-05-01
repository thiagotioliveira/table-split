package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionCategoryModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.PromotionItemModel;
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
  private final GetCombos getCombos;
  private final GetCoupons getCoupons;
  private final GetCategory getCategory;
  private final GetItem getItem;

  public PromotionsController(
      GetPromotions getPromotions,
      GetCombos getCombos,
      GetCoupons getCoupons,
      GetCategory getCategory,
      GetItem getItem) {
    this.getPromotions = getPromotions;
    this.getCombos = getCombos;
    this.getCoupons = getCoupons;
    this.getCategory = getCategory;
    this.getItem = getItem;
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
    model.addAttribute("userLanguage", context.getUser().getLanguage().getLabel());

    return "promotions";
  }
}
