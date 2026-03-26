package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Promotion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetPromotions {

  private final PromotionRepository promotionRepository;

  public GetPromotions(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
  }

  public List<Promotion> listByRestaurantId(UUID restaurantId) {
    return promotionRepository.findByRestaurantId(restaurantId);
  }

  public Optional<Promotion> findById(UUID id) {
    return promotionRepository.findById(id);
  }
}
