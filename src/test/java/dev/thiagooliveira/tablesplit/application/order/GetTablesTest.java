package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTablesTest {

  @Mock private TableRepository tableRepository;

  private GetTables getTables;

  @BeforeEach
  void setUp() {
    getTables = new GetTables(tableRepository);
  }

  @Test
  void shouldReturnTablesForRestaurant() {
    UUID restaurantId = UUID.randomUUID();
    List<Table> expectedTables =
        List.of(
            new Table(UUID.randomUUID(), restaurantId, "01"),
            new Table(UUID.randomUUID(), restaurantId, "02"));

    when(tableRepository.findAllByRestaurantId(restaurantId)).thenReturn(expectedTables);

    GetTables.Result result = getTables.execute(restaurantId);

    assertEquals(expectedTables, result.tables());
    assertEquals(2, result.count());
    assertEquals(2, result.countAvailable());
    assertEquals(0, result.countOccupied());
    verify(tableRepository).findAllByRestaurantId(restaurantId);
  }

  @Test
  void shouldReturnEmptyListWhenNoTablesExist() {
    UUID restaurantId = UUID.randomUUID();
    when(tableRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of());

    GetTables.Result result = getTables.execute(restaurantId);

    assertTrue(result.tables().isEmpty());
    assertEquals(0, result.count());
    verify(tableRepository).findAllByRestaurantId(restaurantId);
  }

  @Test
  void shouldCountOccupiedTables() {
    UUID restaurantId = UUID.randomUUID();
    Table t1 = new Table(UUID.randomUUID(), restaurantId, "01");
    Table t2 = new Table(UUID.randomUUID(), restaurantId, "02");
    t2.setStatus(TableStatus.OCCUPIED);

    when(tableRepository.findAllByRestaurantId(restaurantId)).thenReturn(List.of(t1, t2));

    GetTables.Result result = getTables.execute(restaurantId);

    assertEquals(2, result.count());
    assertEquals(1, result.countAvailable());
    assertEquals(1, result.countOccupied());
  }
}
