package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.application.account.PlanLimitType;
import dev.thiagooliveira.tablesplit.application.account.PlanLimitValidator;
import dev.thiagooliveira.tablesplit.application.image.ImageStorage;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateItemCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.menu.ItemRepository;
import java.util.UUID;

public class CreateItem {

  private final ImageStorage imageStorage;
  private final ItemRepository itemRepository;
  private final PlanLimitValidator planLimitValidator;
  private final long maxImageSize;

  public CreateItem(
      ImageStorage imageStorage,
      ItemRepository itemRepository,
      PlanLimitValidator planLimitValidator,
      long maxImageSize) {
    this.imageStorage = imageStorage;
    this.itemRepository = itemRepository;
    this.planLimitValidator = planLimitValidator;
    this.maxImageSize = maxImageSize;
  }

  public Item execute(UUID accountId, UUID restaurantId, CreateItemCommand command) {
    this.planLimitValidator.validate(
        accountId, PlanLimitType.MENU_ITEMS, this.itemRepository.count(restaurantId));

    var item = Item.create(accountId, restaurantId);
    item.setName(command.name());
    item.setDescription(command.description());
    item.setPrice(command.price());
    item.setCategoryId(command.categoryId());
    item.setAvailable(command.available());
    item.setTags(command.tags());
    item.setQuestions(command.questions());

    if (command.images() != null && command.images().newImages() != null) {
      for (var imageData : command.images().newImages()) {
        if (imageData.content().length > maxImageSize) {
          throw new IllegalArgumentException("error.image.too_large");
        }
        UUID imageId = UUID.randomUUID();
        String imagePath =
            imageStorage.uploadItem(imageData, accountId, restaurantId, item.getId(), imageId);
        item.addImage(imageId, imagePath, item.getImages().isEmpty());
      }
    }

    this.itemRepository.save(item);

    return item;
  }
}
