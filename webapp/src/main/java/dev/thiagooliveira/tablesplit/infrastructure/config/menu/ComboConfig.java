package dev.thiagooliveira.tablesplit.infrastructure.config.menu;

import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComboConfig {

  @Bean
  public CreateCombo createCombo(ComboRepository comboRepository) {
    return new CreateCombo(comboRepository);
  }

  @Bean
  public UpdateCombo updateCombo(ComboRepository comboRepository) {
    return new UpdateCombo(comboRepository);
  }

  @Bean
  public DeleteCombo deleteCombo(ComboRepository comboRepository) {
    return new DeleteCombo(comboRepository);
  }

  @Bean
  public GetCombos getCombos(ComboRepository comboRepository) {
    return new GetCombos(comboRepository);
  }

  @Bean
  public ToggleCombo toggleCombo(ComboRepository comboRepository) {
    return new ToggleCombo(comboRepository);
  }
}
