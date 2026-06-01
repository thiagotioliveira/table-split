package dev.thiagooliveira.tablesplit.application.menu;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.menu.command.ItemImageCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ItemImageDataCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateItemTest {

  private ItemImageStorage itemImageStorage;
  private ItemRepository itemRepository;
  private UpdateItem updateItem;
  private long maxImageSize = 1000L;

  @BeforeEach
  void setUp() {
    itemImageStorage = mock(ItemImageStorage.class);
    itemRepository = mock(ItemRepository.class);
    updateItem = new UpdateItem(itemImageStorage, itemRepository, maxImageSize);
  }

  @Test
  void shouldUpdateItemSuccessfully() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Item existingItem = new Item();
    existingItem.setId(itemId);
    existingItem.setAccountId(accountId);
    existingItem.setRestaurantId(restaurantId);
    existingItem.setImages(new ArrayList<>());

    UpdateItemCommand command =
        new UpdateItemCommand(
            itemId,
            categoryId,
            Collections.emptyList(),
            new ItemImageCommand(Collections.emptyList(), Collections.emptyList()),
            Map.of(Language.PT, "Novo Nome"),
            Map.of(Language.PT, "Nova Descricao"),
            BigDecimal.valueOf(32.90),
            Collections.emptyList(),
            true,
            Collections.emptyMap());

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

    Item updated = updateItem.execute(accountId, restaurantId, itemId, command);

    assertNotNull(updated);
    assertEquals(BigDecimal.valueOf(32.90), updated.getPrice());
    assertEquals("Novo Nome", updated.getName().get(Language.PT));
    assertEquals(categoryId, updated.getCategoryId());

    verify(itemRepository).save(existingItem);
  }

  @Test
  void shouldThrowExceptionWhenTenantAccessDenied() {
    UUID accountId = UUID.randomUUID();
    UUID differentAccountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Item existingItem = new Item();
    existingItem.setId(itemId);
    existingItem.setAccountId(differentAccountId);
    existingItem.setRestaurantId(restaurantId);

    UpdateItemCommand command =
        new UpdateItemCommand(
            itemId,
            null,
            Collections.emptyList(),
            null,
            Map.of(Language.PT, "Nome"),
            Map.of(Language.PT, "Desc"),
            BigDecimal.TEN,
            Collections.emptyList(),
            true,
            Collections.emptyMap());

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

    assertThrows(
        IllegalArgumentException.class,
        () -> updateItem.execute(accountId, restaurantId, itemId, command));

    verify(itemRepository, never()).save(any());
  }

  @Test
  void shouldDeleteOutdatedImagesAndKeepSpecifiedOnes() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Item existingItem = new Item();
    existingItem.setId(itemId);
    existingItem.setAccountId(accountId);
    existingItem.setRestaurantId(restaurantId);

    ItemImage img1 = new ItemImage();
    img1.setId(UUID.randomUUID());
    img1.setName("path1");
    img1.setMain(true);

    ItemImage img2 = new ItemImage();
    img2.setId(UUID.randomUUID());
    img2.setName("path2");
    img2.setMain(false);

    List<ItemImage> imagesList = new ArrayList<>();
    imagesList.add(img1);
    imagesList.add(img2);
    existingItem.setImages(imagesList);

    // Command specifies to only keep img2
    UpdateItemCommand command =
        new UpdateItemCommand(
            itemId,
            null,
            List.of(img2.getId()),
            new ItemImageCommand(List.of(img2.getId()), Collections.emptyList()),
            Map.of(Language.PT, "Nome"),
            Map.of(Language.PT, "Desc"),
            BigDecimal.TEN,
            Collections.emptyList(),
            true,
            Collections.emptyMap());

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

    Item updated = updateItem.execute(accountId, restaurantId, itemId, command);

    assertNotNull(updated);
    assertEquals(1, updated.getImages().size());
    assertEquals(img2.getId(), updated.getImages().get(0).getId());

    verify(itemImageStorage).delete(accountId, restaurantId, itemId, img1.getId());
    verify(itemImageStorage, never()).delete(accountId, restaurantId, itemId, img2.getId());
    verify(itemRepository).save(existingItem);
  }

  @Test
  void shouldThrowExceptionWhenImageExceedsMaxSizeLimit() {
    UUID accountId = UUID.randomUUID();
    UUID restaurantId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();

    Item existingItem = new Item();
    existingItem.setId(itemId);
    existingItem.setAccountId(accountId);
    existingItem.setRestaurantId(restaurantId);
    existingItem.setImages(new ArrayList<>());

    ItemImageDataCommand tooLarge =
        new ItemImageDataCommand(
            "test.png", "image/png", new byte[1001]); // 1001 bytes > 1000 max size

    UpdateItemCommand command =
        new UpdateItemCommand(
            itemId,
            null,
            Collections.emptyList(),
            new ItemImageCommand(Collections.emptyList(), List.of(tooLarge)),
            Map.of(Language.PT, "Nome"),
            Map.of(Language.PT, "Desc"),
            BigDecimal.TEN,
            Collections.emptyList(),
            true,
            Collections.emptyMap());

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

    assertThrows(
        IllegalArgumentException.class,
        () -> updateItem.execute(accountId, restaurantId, itemId, command));

    verify(itemImageStorage, never()).upload(any(), any(), any(), any(), any());
    verify(itemRepository, never()).save(any());
  }
}
