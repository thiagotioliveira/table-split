package dev.thiagooliveira.tablesplit.application.image;

import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import java.util.Map;
import java.util.UUID;

public interface ImageStorage {

  String uploadItem(ImageData image, UUID accountId, UUID restaurantId, UUID itemId, UUID imageId);

  Map<String, Object> deleteItem(UUID accountId, UUID restaurantId, UUID itemId, UUID imageId);

  String uploadRestaurantGallery(ImageData image, UUID accountId, UUID restaurantId, UUID imageId);

  Map<String, Object> deleteRestaurantGallery(UUID accountId, UUID restaurantId, UUID imageId);
}
