package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateTableTest {

  @Mock private TableRepository tableRepository;
  @Mock private PlanLimitValidator planLimitValidator;

  private CreateTable createTable;

  @BeforeEach
  void setUp() {
    createTable = new CreateTable(tableRepository, planLimitValidator);
  }

  @Test
  void shouldCreateTableSuccessfully() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";

    when(tableRepository.count(restaurantId)).thenReturn(5L);
    when(tableRepository.findByRestaurantIdAndCod(restaurantId, cod)).thenReturn(Optional.empty());
    when(tableRepository.findByRestaurantIdAndCodIncludingDeleted(restaurantId, cod))
        .thenReturn(Optional.empty());

    createTable.execute(accountId, restaurantId, cod);

    ArgumentCaptor<Table> tableCaptor = ArgumentCaptor.forClass(Table.class);
    verify(tableRepository).save(tableCaptor.capture());
    verify(planLimitValidator).validate(eq(accountId), eq(PlanLimitType.TABLES), eq(5L));

    Table savedTable = tableCaptor.getValue();
    assertEquals(cod, savedTable.getCod());
    assertEquals(restaurantId, savedTable.getRestaurantId());
    assertNotNull(savedTable.getId());
  }

  @Test
  void shouldThrowExceptionWhenTableAlreadyExists() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";

    Table existingTable = new Table(UUID.randomUUID(), restaurantId, cod);

    when(tableRepository.count(restaurantId)).thenReturn(5L);
    when(tableRepository.findByRestaurantIdAndCod(restaurantId, cod))
        .thenReturn(Optional.of(existingTable));

    assertThrows(TableAlreadyExists.class, () -> createTable.execute(accountId, restaurantId, cod));
    verify(tableRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionWhenLimitReached() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";

    when(tableRepository.count(restaurantId)).thenReturn(20L);
    doThrow(new PlanLimitExceededException("error.plan.limit.tables"))
        .when(planLimitValidator)
        .validate(eq(accountId), eq(PlanLimitType.TABLES), eq(20L));

    assertThrows(
        PlanLimitExceededException.class, () -> createTable.execute(accountId, restaurantId, cod));
    verify(tableRepository, never()).save(any());
  }

  @Test
  void shouldResurrectTableWhenUnderLimit() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";
    Table deletedTable = new Table(UUID.randomUUID(), restaurantId, cod);
    deletedTable.softDelete();

    when(tableRepository.count(restaurantId)).thenReturn(5L);
    when(tableRepository.findByRestaurantIdAndCod(restaurantId, cod)).thenReturn(Optional.empty());
    when(tableRepository.findByRestaurantIdAndCodIncludingDeleted(restaurantId, cod))
        .thenReturn(Optional.of(deletedTable));

    createTable.execute(accountId, restaurantId, cod);

    assertFalse(deletedTable.isDeleted());
    verify(tableRepository).save(deletedTable);
    verify(planLimitValidator).validate(eq(accountId), eq(PlanLimitType.TABLES), eq(5L));
  }
}
