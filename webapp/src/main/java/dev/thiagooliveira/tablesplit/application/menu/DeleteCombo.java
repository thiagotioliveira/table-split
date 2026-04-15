package dev.thiagooliveira.tablesplit.application.menu;

import java.util.UUID;

public class DeleteCombo {

  private final ComboRepository comboRepository;

  public DeleteCombo(ComboRepository comboRepository) {
    this.comboRepository = comboRepository;
  }

  public void execute(UUID id) {
    comboRepository.deleteById(id);
  }
}
