package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.Map;
import java.util.UUID;

public interface ItemImageStorage {
  String upload(ItemImageData image, UUID accountId, UUID restaurantId, UUID itemId, UUID imageId);

  Map<String, Object> delete(UUID accountId, UUID restaurantId, UUID itemId, UUID imageId);
}
