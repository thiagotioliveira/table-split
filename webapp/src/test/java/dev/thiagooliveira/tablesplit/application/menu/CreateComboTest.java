package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateComboCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateComboTest {

  private ComboRepository comboRepository;
  private CreateCombo createCombo;

  @BeforeEach
  void setUp() {
    comboRepository = mock(ComboRepository.class);
    createCombo = new CreateCombo(comboRepository);
  }

  @Test
  void shouldCreateComboSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    var command =
        new CreateComboCommand(
            "Burger Combo",
            "Burger + fries + drink",
            BigDecimal.valueOf(15.99),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(30),
            List.of(),
            true);

    when(comboRepository.save(any(Combo.class))).thenAnswer(inv -> inv.getArgument(0));

    Combo result = createCombo.execute(restaurantId, command);

    assertNotNull(result.getId());
    assertEquals(restaurantId, result.getRestaurantId());
    assertEquals("Burger Combo", result.getName());
    assertEquals("Burger + fries + drink", result.getDescription());
    assertEquals(BigDecimal.valueOf(15.99), result.getComboPrice());
    assertTrue(result.isActive());
    verify(comboRepository).save(any(Combo.class));
  }
}
