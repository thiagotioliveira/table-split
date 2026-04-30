package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import java.util.UUID;
import java.util.stream.Collectors;

public class UpdateItem {

  private final ImageStorage imageStorage;
  private final ItemRepository itemRepository;
  private final long maxImageSize;

  public UpdateItem(ImageStorage imageStorage, ItemRepository itemRepository, long maxImageSize) {
    this.imageStorage = imageStorage;
    this.itemRepository = itemRepository;
    this.maxImageSize = maxImageSize;
  }

  public Item execute(UUID accountId, UUID restaurantId, UUID itemId, UpdateItemCommand command) {
    var item = this.itemRepository.findById(itemId).orElseThrow();
    if (!item.getAccountId().equals(accountId) || !item.getRestaurantId().equals(restaurantId)) {
      throw new IllegalArgumentException("Access denied");
    }
    item.setName(command.name());
    item.setDescription(command.description());
    item.setPrice(command.price());
    item.setCategoryId(command.categoryId());
    item.setAvailable(command.available());
    item.setTags(command.tags());
    item.setQuestions(command.questions());

    // Handle images to delete
    var currentImages = item.getImages();
    var imagesToKeepIds = command.imageIdsToKeep();
    var imagesToDelete =
        currentImages.stream()
            .filter(img -> !imagesToKeepIds.contains(img.getId()))
            .collect(Collectors.toList());

    for (var img : imagesToDelete) {
      imageStorage.deleteItem(accountId, restaurantId, itemId, img.getId());
    }
    item.setImages(
        currentImages.stream()
            .filter(img -> imagesToKeepIds.contains(img.getId()))
            .collect(Collectors.toList()));

    // Handle new images
    if (command.images() != null && command.images().newImages() != null) {
      for (var imageData : command.images().newImages()) {
        if (imageData.content().length > maxImageSize) {
          throw new IllegalArgumentException("error.image.too_large");
        }
        UUID imageId = UUID.randomUUID();
        String imagePath =
            imageStorage.uploadItem(imageData, accountId, restaurantId, itemId, imageId);
        item.addImage(imageId, imagePath, item.getImages().isEmpty());
      }
    }

    this.itemRepository.save(item);

    return item;
  }
}
