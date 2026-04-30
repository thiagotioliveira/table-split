package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComboRepository {
  Combo save(Combo combo);

  Optional<Combo> findById(UUID id);

  List<Combo> findByRestaurantId(UUID restaurantId);

  void deleteById(UUID id);
}
