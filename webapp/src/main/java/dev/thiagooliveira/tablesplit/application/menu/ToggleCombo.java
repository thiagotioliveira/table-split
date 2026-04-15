package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class ToggleCombo {

  private final ComboRepository comboRepository;

  public ToggleCombo(ComboRepository comboRepository) {
    this.comboRepository = comboRepository;
  }

  public void execute(UUID id) {
    comboRepository
        .findById(id)
        .ifPresent(
            combo -> {
              combo.setActive(!combo.isActive());
              comboRepository.save(combo);
            });
  }
}
