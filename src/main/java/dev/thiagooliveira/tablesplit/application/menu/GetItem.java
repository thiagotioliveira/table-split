package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

  public Optional<Item> findById(UUID itemId) {
    return this.itemRepository.findById(itemId);
  }

  public Optional<Item> findByIdIncludingDeleted(UUID itemId) {
    return this.itemRepository.findByIdIncludingDeleted(itemId);
  }

  public List<Item> execute(
      UUID restaurantId, List<Language> languages, boolean includePromotions) {
    var items = this.itemRepository.findAll(restaurantId, languages);
    if (includePromotions) {
      var now = LocalDateTime.now();
      var today = now.getDayOfWeek();
      var currentTime = now.toLocalTime();

      var activePromos =
          promotionRepository.findByRestaurantId(restaurantId).stream()
              .filter(Promotion::isActive)
              .filter(p -> p.getStartDate() == null || p.getStartDate().isBefore(now))
              .filter(p -> p.getEndDate() == null || p.getEndDate().isAfter(now))
              .filter(
                  p ->
                      p.getDaysOfWeek() == null
                          || p.getDaysOfWeek().isEmpty()
                          || p.getDaysOfWeek().contains(today))
              .filter(
                  p -> {
                    if (p.getStartTime() == null || p.getEndTime() == null) return true;
                    return !currentTime.isBefore(p.getStartTime())
                        && !currentTime.isAfter(p.getEndTime());
                  })
              .toList();

      for (var item : items) {
        findBestPromotion(item, activePromos)
            .ifPresent(
                p -> {
                  item.setPromotion(
                      new Item.PromotionInfo(
                          p.getId(),
                          calculatePromotionalPrice(item.getPrice(), p),
                          p.getDiscountType(),
                          p.getDiscountValue()));
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
    if (p.getApplyType() == ApplyType.ALL_MENU) return true;
    if (p.getApplyType() == ApplyType.CATEGORY) {
      return item.getCategory() != null
          && p.getApplicableIds() != null
          && p.getApplicableIds().contains(item.getCategory().getId().toString());
    }
    if (p.getApplyType() == ApplyType.ITEM) {
      return p.getApplicableIds() != null && p.getApplicableIds().contains(item.getId().toString());
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
