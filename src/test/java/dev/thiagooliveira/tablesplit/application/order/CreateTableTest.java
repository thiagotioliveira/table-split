package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.order.exception.TableAlreadyExists;
import dev.thiagooliveira.tablesplit.domain.order.Table;
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

  private CreateTable createTable;

  @BeforeEach
  void setUp() {
    createTable = new CreateTable(tableRepository);
  }

  @Test
  void shouldCreateTableSuccessfully() {
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, cod)).thenReturn(Optional.empty());

    createTable.execute(restaurantId, cod);

    ArgumentCaptor<Table> tableCaptor = ArgumentCaptor.forClass(Table.class);
    verify(tableRepository).save(tableCaptor.capture());

    Table savedTable = tableCaptor.getValue();
    assertEquals(cod, savedTable.getCod());
    assertEquals(restaurantId, savedTable.getRestaurantId());
    assertNotNull(savedTable.getId());
  }

  @Test
  void shouldThrowExceptionWhenTableAlreadyExists() {
    UUID restaurantId = UUID.randomUUID();
    String cod = "01";
    Table existingTable = new Table(UUID.randomUUID(), restaurantId, cod);

    when(tableRepository.findByRestaurantIdAndCod(restaurantId, cod))
        .thenReturn(Optional.of(existingTable));

    assertThrows(TableAlreadyExists.class, () -> createTable.execute(restaurantId, cod));
    verify(tableRepository, never()).save(any());
  }
}
