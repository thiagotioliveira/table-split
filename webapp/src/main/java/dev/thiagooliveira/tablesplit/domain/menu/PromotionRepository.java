package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository {
  Promotion save(Promotion promotion);

  Optional<Promotion> findById(UUID id);

  List<Promotion> findByRestaurantId(UUID restaurantId);

  void deleteById(UUID id);

  long count(UUID restaurantId);
}
