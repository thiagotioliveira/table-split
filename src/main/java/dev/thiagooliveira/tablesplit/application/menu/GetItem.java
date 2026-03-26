package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetItem {

  private final ItemRepository itemRepository;
  private final PromotionRepository promotionRepository;

  public GetItem(ItemRepository itemRepository, PromotionRepository promotionRepository) {
    this.itemRepository = itemRepository;
    this.promotionRepository = promotionRepository;
  }

  public List<Item> execute(UUID restaurantId, List<Language> languages) {
    return this.execute(restaurantId, languages, false);
  }

  public List<Item> execute(
      UUID restaurantId, List<Language> languages, boolean includePromotions) {
    var items = this.itemRepository.findAll(restaurantId, languages);
    if (includePromotions) {
      var activePromos =
          promotionRepository.findByRestaurantId(restaurantId).stream()
              .filter(Promotion::isActive)
              .toList();

      for (var item : items) {
        findBestPromotion(item, activePromos)
            .ifPresent(
                p -> {
                  item.setPromotion(
                      new Item.PromotionInfo(
                          p.getId(), calculatePromotionalPrice(item.getPrice(), p)));
                });
      }
    }
    return items;
  }

  private Optional<Promotion> findBestPromotion(Item item, List<Promotion> activePromos) {
    return activePromos.stream()
        .filter(p -> isApplicable(item, p))
        .min(Comparator.comparing(p -> calculatePromotionalPrice(item.getPrice(), p)));
  }

  private boolean isApplicable(Item item, Promotion p) {
    if (p.getApplyType() == ApplyType.MENU) return true;
    if (p.getApplyType() == ApplyType.CATEGORY) {
      return item.getCategory() != null && item.getCategory().getId().equals(p.getApplicableId());
    }
    if (p.getApplyType() == ApplyType.ITEM) {
      return item.getId().equals(p.getApplicableId());
    }
    return false;
  }

  private BigDecimal calculatePromotionalPrice(BigDecimal originalPrice, Promotion p) {
    if (originalPrice == null) return BigDecimal.ZERO;
    if (p.getDiscountType() == DiscountType.PERCENTAGE) {
      var discount =
          originalPrice
              .multiply(p.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      return originalPrice.subtract(discount);
    } else {
      return originalPrice.subtract(p.getDiscountValue()).max(BigDecimal.ZERO);
    }
  }

  public long count(UUID restaurantId) {
    return this.itemRepository.count(restaurantId);
  }

  public long countActive(UUID restaurantId) {
    return this.itemRepository.countActive(restaurantId);
  }

  public long countInactive(UUID restaurantId) {
    return this.itemRepository.countInactive(restaurantId);
  }
}
