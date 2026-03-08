package dev.thiagooliveira.tablesplit.application.image;

import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import java.util.Map;
import java.util.UUID;

public interface ImageStorage {

  String uploadItem(ImageData image, UUID restaurantId, UUID itemId, UUID imageId);

  Map deleteItem(UUID restaurantId, UUID itemId, UUID imageId);

  Map deleteItem(String imageId);
}
