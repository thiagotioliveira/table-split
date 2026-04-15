package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateComboCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.util.UUID;

public class CreateCombo {

  private final ComboRepository comboRepository;

  public CreateCombo(ComboRepository comboRepository) {
    this.comboRepository = comboRepository;
  }

  public Combo execute(UUID restaurantId, CreateComboCommand command) {
    var combo = new Combo();
    combo.setId(UUID.randomUUID());
    combo.setRestaurantId(restaurantId);
    combo.setName(command.name());
    combo.setDescription(command.description());
    combo.setComboPrice(command.comboPrice());
    combo.setStartDate(command.startDate());
    combo.setEndDate(command.endDate());
    combo.setItems(command.items());
    combo.setActive(command.active());

    return comboRepository.save(combo);
  }
}
