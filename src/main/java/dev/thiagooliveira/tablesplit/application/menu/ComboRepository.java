package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComboRepository {
  Combo save(Combo combo);

  Optional<Combo> findById(UUID id);

  List<Combo> findByRestaurantId(UUID restaurantId);

  void deleteById(UUID id);
}
