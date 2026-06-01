package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdateComboCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import dev.thiagooliveira.tablesplit.domain.menu.ComboRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateComboTest {

  private ComboRepository comboRepository;
  private UpdateCombo updateCombo;

  @BeforeEach
  void setUp() {
    comboRepository = mock(ComboRepository.class);
    updateCombo = new UpdateCombo(comboRepository);
  }

  @Test
  void shouldUpdateComboSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID comboId = UUID.randomUUID();
    Combo existing = new Combo();
    existing.setId(comboId);

    var command =
        new UpdateComboCommand(
            "Updated Combo",
            "New description",
            BigDecimal.valueOf(19.99),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(14),
            List.of(),
            false);

    when(comboRepository.findById(comboId)).thenReturn(Optional.of(existing));
    when(comboRepository.save(existing)).thenReturn(existing);

    Combo result = updateCombo.execute(restaurantId, comboId, command);

    assertEquals("Updated Combo", result.getName());
    assertEquals("New description", result.getDescription());
    assertEquals(BigDecimal.valueOf(19.99), result.getComboPrice());
    assertFalse(result.isActive());
    verify(comboRepository).save(existing);
  }

  @Test
  void shouldThrowWhenComboNotFound() {
    UUID restaurantId = UUID.randomUUID();
    UUID comboId = UUID.randomUUID();
    var command = new UpdateComboCommand("X", null, BigDecimal.ZERO, null, null, List.of(), true);

    when(comboRepository.findById(comboId)).thenReturn(Optional.empty());

    assertThrows(
        java.util.NoSuchElementException.class,
        () -> updateCombo.execute(restaurantId, comboId, command));
  }
}
