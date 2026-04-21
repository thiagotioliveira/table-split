package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.common.Time;
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
    return execute(restaurantId, languages, false);
  }

  public List<Item> execute(
      UUID restaurantId, List<Language> languages, boolean includePromotions) {
    var items = itemRepository.findAll(restaurantId, languages);
    if (includePromotions) {
      var activePromos = activePromotions(restaurantId);
      items.forEach(item -> applyBestPromotion(item, activePromos));
    }
    return items;
  }

  public Optional<Item> findByIdIncludingDeleted(UUID itemId, boolean includePromotions) {
    var itemOpt = itemRepository.findByIdIncludingDeleted(itemId);
    if (itemOpt.isPresent() && includePromotions) {
      var item = itemOpt.get();
      var activePromos = activePromotions(item.getRestaurantId());
      applyBestPromotion(item, activePromos);
    }
    return itemOpt;
  }

  public long count(UUID restaurantId) {
    return itemRepository.count(restaurantId);
  }

  public long countActive(UUID restaurantId) {
    return itemRepository.countActive(restaurantId);
  }

  public long countInactive(UUID restaurantId) {
    return itemRepository.countInactive(restaurantId);
  }

  // ── Private helpers ──────────────────────────────────────────────────────────

  private List<Promotion> activePromotions(UUID restaurantId) {
    var now = Time.nowLocalDateTime();
    var today = now.getDayOfWeek();
    var currentTime = now.toLocalTime();

    return promotionRepository.findByRestaurantId(restaurantId).stream()
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
  }

  private void applyBestPromotion(Item item, List<Promotion> activePromos) {
    findBestPromotion(item, activePromos)
        .ifPresent(
            p ->
                item.setPromotion(
                    new Item.PromotionInfo(
                        p.getId(),
                        calculatePromotionalPrice(item.getPrice(), p),
                        p.getDiscountType(),
                        p.getDiscountValue())));
  }

  private Optional<Promotion> findBestPromotion(Item item, List<Promotion> activePromos) {
    return activePromos.stream()
        .filter(p -> isApplicable(item, p))
        .min(Comparator.comparing(p -> calculatePromotionalPrice(item.getPrice(), p)));
  }

  private boolean isApplicable(Item item, Promotion p) {
    return switch (p.getApplyType()) {
      case ALL_MENU -> true;
      case CATEGORY ->
          item.getCategory() != null
              && p.getApplicableIds() != null
              && p.getApplicableIds().contains(item.getCategory().getId().toString());
      case ITEM ->
          p.getApplicableIds() != null && p.getApplicableIds().contains(item.getId().toString());
    };
  }

  private BigDecimal calculatePromotionalPrice(BigDecimal originalPrice, Promotion p) {
    if (originalPrice == null) return BigDecimal.ZERO;
    if (p.getDiscountType() == DiscountType.PERCENTAGE) {
      var discount =
          originalPrice
              .multiply(p.getDiscountValue())
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      return originalPrice.subtract(discount);
    }
    return originalPrice.subtract(p.getDiscountValue()).max(BigDecimal.ZERO);
  }
}
