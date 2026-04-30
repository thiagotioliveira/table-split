package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import java.util.UUID;

public class TogglePromotion {

  private final PromotionRepository promotionRepository;

  public TogglePromotion(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public void execute(UUID id) {
    promotionRepository
        .findById(id)
        .ifPresent(
            promotion -> {
              promotion.setActive(!promotion.isActive());
              promotionRepository.save(promotion);
            });
  }
}
