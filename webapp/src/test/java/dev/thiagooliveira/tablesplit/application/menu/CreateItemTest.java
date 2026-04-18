package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateItemCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateItemTest {

  @Mock private EventPublisher eventPublisher;
  @Mock private ImageStorage imageStorage;
  @Mock private ItemRepository itemRepository;
  @Mock private PlanLimitValidator planLimitValidator;

  private CreateItem createItem;
  private final long MAX_IMAGE_SIZE = 1024 * 1024;

  @BeforeEach
  void setUp() {
    createItem =
        new CreateItem(
            eventPublisher, imageStorage, itemRepository, planLimitValidator, MAX_IMAGE_SIZE);
  }

  @Test
  void shouldThrowException_whenItemLimitReached() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();

    CreateItemCommand command =
        new CreateItemCommand(
            UUID.randomUUID(),
            List.of(),
            null,
            Map.of(Language.PT, "Batatas"),
            Map.of(Language.PT, "Deliciosas"),
            BigDecimal.TEN,
            List.of(),
            true);

    doThrow(new PlanLimitExceededException("error.plan.limit.menu_items"))
        .when(planLimitValidator)
        .validate(eq(accountId), any(), anyLong());
    when(itemRepository.count(restaurantId)).thenReturn(40L);

    assertThrows(
        PlanLimitExceededException.class,
        () -> createItem.execute(accountId, restaurantId, command));

    verify(itemRepository, never()).save(any());
  }

  @Test
  void shouldAllowCreation_whenUnderLimit() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();

    CreateItemCommand command =
        new CreateItemCommand(
            UUID.randomUUID(),
            List.of(),
            null,
            Map.of(Language.PT, "Batatas"),
            Map.of(Language.PT, "Deliciosas"),
            BigDecimal.TEN,
            List.of(),
            true);

    when(itemRepository.count(restaurantId)).thenReturn(39L);
    when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    createItem.execute(accountId, restaurantId, command);

    verify(itemRepository).save(any());
    verify(planLimitValidator).validate(eq(accountId), any(), eq(39L));
  }
}
