package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetCombos {

  private final ComboRepository comboRepository;

  public GetCombos(ComboRepository comboRepository) {
    this.comboRepository = comboRepository;
  }

  public List<Combo> listByRestaurantId(UUID restaurantId) {
    return comboRepository.findByRestaurantId(restaurantId);
  }

  public Optional<Combo> findById(UUID id) {
    return comboRepository.findById(id);
  }
}
