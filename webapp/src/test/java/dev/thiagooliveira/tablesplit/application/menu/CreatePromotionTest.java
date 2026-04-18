package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.menu.command.CreatePromotionCommand;
import dev.thiagooliveira.tablesplit.domain.menu.ApplyType;
import dev.thiagooliveira.tablesplit.domain.menu.DiscountType;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePromotionTest {

  @Mock private PromotionRepository promotionRepository;
  @Mock private PlanLimitValidator planLimitValidator;

  private CreatePromotion createPromotion;

  @BeforeEach
  void setUp() {
    createPromotion = new CreatePromotion(promotionRepository, planLimitValidator);
  }

  @Test
  void shouldThrowException_whenLimitReached() {
    UUID restaurantId = UUID.randomUUID();
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Promo",
            "Desc",
            DiscountType.PERCENTAGE,
            BigDecimal.TEN,
            BigDecimal.ZERO,
            null,
            null,
            Set.of(),
            null,
            null,
            ApplyType.CATEGORY,
            Set.of(),
            true);

    when(promotionRepository.count(restaurantId)).thenReturn(5L);
    doThrow(new PlanLimitExceededException("error.plan.limit.promotions"))
        .when(planLimitValidator)
        .validateByRestaurantId(eq(restaurantId), any(), eq(5L));

    assertThrows(
        PlanLimitExceededException.class, () -> createPromotion.execute(restaurantId, command));
    verify(promotionRepository, never()).save(any());
  }

  @Test
  void shouldAllowCreation_whenUnderLimit() {
    UUID restaurantId = UUID.randomUUID();
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Promo",
            "Desc",
            DiscountType.PERCENTAGE,
            BigDecimal.TEN,
            BigDecimal.ZERO,
            null,
            null,
            Set.of(),
            null,
            null,
            ApplyType.CATEGORY,
            Set.of(),
            true);

    when(promotionRepository.count(restaurantId)).thenReturn(4L);
    when(promotionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    createPromotion.execute(restaurantId, command);

    verify(promotionRepository).save(any());
    verify(planLimitValidator).validateByRestaurantId(eq(restaurantId), any(), eq(4L));
  }
}
