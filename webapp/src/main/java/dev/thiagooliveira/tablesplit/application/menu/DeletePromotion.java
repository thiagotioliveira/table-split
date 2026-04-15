package dev.thiagooliveira.tablesplit.application.menu;

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
