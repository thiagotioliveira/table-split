package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCategoryTest {

  @Mock private CategoryRepository categoryRepository;
  @Mock private EventPublisher eventPublisher;
  @Mock private PlanLimitValidator planLimitValidator;

  private CreateCategory createCategory;

  @BeforeEach
  void setUp() {
    createCategory = new CreateCategory(eventPublisher, categoryRepository, planLimitValidator);
  }

  @Test
  void shouldThrowException_whenLimitReached() {
    UUID restaurantId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();
    CreateCategoryCommand command = new CreateCategoryCommand(Map.of(), 1);

    when(categoryRepository.count(restaurantId)).thenReturn(10L);
    doThrow(new PlanLimitExceededException("error.plan.limit.categories"))
        .when(planLimitValidator)
        .validate(eq(accountId), any(), eq(10L));

    assertThrows(
        PlanLimitExceededException.class,
        () -> createCategory.execute(accountId, restaurantId, command));
    verify(categoryRepository, never()).save(any());
  }

  @Test
  void shouldAllowCreation_whenUnderLimit() {
    UUID restaurantId = UUID.randomUUID();
    UUID accountId = UUID.randomUUID();
    CreateCategoryCommand command = new CreateCategoryCommand(Map.of(), 1);

    when(categoryRepository.count(restaurantId)).thenReturn(9L);

    createCategory.execute(accountId, restaurantId, command);

    verify(categoryRepository).save(any());
    verify(planLimitValidator).validate(eq(accountId), any(), eq(9L));
  }
}
