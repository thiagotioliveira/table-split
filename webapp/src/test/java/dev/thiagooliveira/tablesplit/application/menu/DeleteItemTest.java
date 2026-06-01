package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteItemTest {

  private ItemRepository itemRepository;
  private ItemImageStorage itemImageStorage;
  private DeleteItem deleteItem;

  @BeforeEach
  void setUp() {
    itemRepository = mock(ItemRepository.class);
    itemImageStorage = mock(ItemImageStorage.class);
    deleteItem = new DeleteItem(itemRepository, itemImageStorage);
  }

  @Test
  void shouldDeleteItemSuccessfully() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Item item = new Item();
    item.setId(itemId);
    item.setAccountId(accountId);
    item.setRestaurantId(restaurantId);

    ItemImage img = new ItemImage();
    img.setId(UUID.randomUUID());
    item.setImages(List.of(img));

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    deleteItem.execute(accountId, restaurantId, itemId);

    verify(itemRepository).delete(itemId);
    verify(itemImageStorage).delete(accountId, restaurantId, itemId, img.getId());
  }

  @Test
  void shouldThrowExceptionWhenAccessDenied() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Item item = new Item();
    item.setId(itemId);
    item.setAccountId(UUID.randomUUID()); // different account
    item.setRestaurantId(restaurantId);

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    assertThrows(
        IllegalArgumentException.class, () -> deleteItem.execute(accountId, restaurantId, itemId));
  }
}
