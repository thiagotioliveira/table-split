package dev.thiagooliveira.tablesplit.application.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.common.DomainException;
import dev.thiagooliveira.tablesplit.domain.order.Table;
import dev.thiagooliveira.tablesplit.domain.order.TableRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteTableTest {

  private TableRepository tableRepository;
  private DeleteTable deleteTable;

  @BeforeEach
  void setUp() {
    tableRepository = mock(TableRepository.class);
    deleteTable = new DeleteTable(tableRepository);
  }

  @Test
  void shouldDeleteTablePhysicallyWhenNoOrdersExist() {
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, UUID.randomUUID(), "T1");

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
    when(tableRepository.hasOrders(tableId)).thenReturn(false);

    deleteTable.execute(tableId);

    verify(tableRepository).delete(tableId);
    verify(tableRepository, never()).save(any(Table.class));
  }

  @Test
  void shouldSoftDeleteTableWhenOrdersExist() {
    UUID tableId = UUID.randomUUID();
    Table table = new Table(tableId, UUID.randomUUID(), "T1");

    when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
    when(tableRepository.hasOrders(tableId)).thenReturn(true);

    deleteTable.execute(tableId);

    assertNotNull(table.getDeletedAt());
    verify(tableRepository).save(table);
    verify(tableRepository, never()).delete(tableId);
  }

  @Test
  void shouldThrowExceptionWhenTableNotFound() {
    UUID tableId = UUID.randomUUID();
    when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

    assertThrows(DomainException.class, () -> deleteTable.execute(tableId));
  }
}
