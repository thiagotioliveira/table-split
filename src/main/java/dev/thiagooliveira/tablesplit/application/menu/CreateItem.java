package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.dto.CreateItemCommand;
import dev.thiagooliveira.tablesplit.application.menu.dto.ImageData;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import java.util.UUID;

public class CreateItem {

  private final ImageStorage imageStorage;
  private final ItemImageRepository itemImageRepository;
  private final ItemRepository itemRepository;

  public CreateItem(
      ImageStorage imageStorage,
      ItemImageRepository itemImageRepository,
      ItemRepository itemRepository) {
    this.imageStorage = imageStorage;
    this.itemImageRepository = itemImageRepository;
    this.itemRepository = itemRepository;
  }

  public void execute(UUID restaurantId, CreateItemCommand command) {

    var item = new Item();
    item.setId(UUID.randomUUID());
    item.setName(command.name());
    item.setRestaurantId(restaurantId);
    item.setDescription(command.description());
    item.setCategoryId(command.categoryId());
    item.setPrice(command.price());
    this.itemRepository.save(item);

    if (command.images() != null) {
      for (ImageData i : command.images().newImages()) {
        var image = new ItemImage();
        image.setId(UUID.randomUUID());
        image.setMain(false);
        image.setItemId(item.getId());
        image.setName(
            imageStorage.uploadItem(i, item.getRestaurantId(), item.getId(), image.getId()));
        this.itemImageRepository.save(image);
      }
    }
  }
}
