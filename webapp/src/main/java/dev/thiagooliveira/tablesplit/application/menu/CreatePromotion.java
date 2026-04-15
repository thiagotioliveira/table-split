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
    promotion.setDaysOfWeek(command.daysOfWeek());
    promotion.setStartTime(command.startTime());
    promotion.setEndTime(command.endTime());
    promotion.setApplyType(command.applyType());
    promotion.setApplicableIds(command.applicableIds());
    promotion.setActive(command.active());

    return promotionRepository.save(promotion);
  }
}
