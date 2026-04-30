package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.PromotionRepository;
import java.util.UUID;

public class DeletePromotion {

  private final PromotionRepository promotionRepository;

  public DeletePromotion(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public void execute(UUID id) {
    promotionRepository.deleteById(id);
  }
}
