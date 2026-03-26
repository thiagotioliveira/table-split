package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.CreatePromotionCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.util.UUID;

public class CreatePromotion {

  private final PromotionRepository promotionRepository;

  public CreatePromotion(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public Promotion execute(UUID restaurantId, CreatePromotionCommand command) {
    var promotion = new Promotion();
    promotion.setId(UUID.randomUUID());
    promotion.setRestaurantId(restaurantId);
    promotion.setName(command.name());
    promotion.setDescription(command.description());
    promotion.setDiscountType(command.discountType());
    promotion.setDiscountValue(command.discountValue());
    promotion.setMinOrderValue(command.minOrderValue());
    promotion.setStartDate(command.startDate());
    promotion.setEndDate(command.endDate());
    promotion.setRecurrence(command.recurrence());
    promotion.setApplyType(command.applyType());
    promotion.setApplicableId(command.applicableId());
    promotion.setActive(command.active());

    return promotionRepository.save(promotion);
  }
}
