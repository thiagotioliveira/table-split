package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.UpdatePromotionCommand;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatePromotionTest {

  private PromotionRepository promotionRepository;
  private UpdatePromotion updatePromotion;

  @BeforeEach
  void setUp() {
    promotionRepository = mock(PromotionRepository.class);
    updatePromotion = new UpdatePromotion(promotionRepository);
  }

  @Test
  void shouldUpdatePromotionSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    UUID promotionId = UUID.randomUUID();
    Promotion existing = new Promotion();
    existing.setId(promotionId);

    var command =
        new UpdatePromotionCommand(
            "Summer Deal",
            "20% off all items",
            DiscountType.PERCENTAGE,
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(10),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(7),
            Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
            LocalTime.of(12, 0),
            LocalTime.of(20, 0),
            ApplyType.ALL_MENU,
            Set.of(),
            true);

    when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(existing));
    when(promotionRepository.save(existing)).thenReturn(existing);

    Promotion result = updatePromotion.execute(restaurantId, promotionId, command);

    assertEquals("Summer Deal", result.getName());
    assertEquals("20% off all items", result.getDescription());
    assertEquals(DiscountType.PERCENTAGE, result.getDiscountType());
    assertTrue(result.isActive());
    verify(promotionRepository).save(existing);
  }

  @Test
  void shouldThrowWhenPromotionNotFound() {
    UUID restaurantId = UUID.randomUUID();
    UUID promotionId = UUID.randomUUID();
    var command =
        new UpdatePromotionCommand(
            "N/A",
            null,
            DiscountType.FIXED_VALUE,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            null,
            Set.of(),
            null,
            null,
            ApplyType.ALL_MENU,
            Set.of(),
            false);

    when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

    assertThrows(
        java.util.NoSuchElementException.class,
        () -> updatePromotion.execute(restaurantId, promotionId, command));
  }
}
