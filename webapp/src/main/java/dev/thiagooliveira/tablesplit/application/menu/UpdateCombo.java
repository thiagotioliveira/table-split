package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateComboCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import java.util.UUID;

public class UpdateCombo {

  private final ComboRepository comboRepository;

  public UpdateCombo(ComboRepository comboRepository) {
    this.comboRepository = comboRepository;
  }

  public Combo execute(UUID restaurantId, UUID comboId, UpdateComboCommand command) {
    var combo = comboRepository.findById(comboId).orElseThrow();
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
