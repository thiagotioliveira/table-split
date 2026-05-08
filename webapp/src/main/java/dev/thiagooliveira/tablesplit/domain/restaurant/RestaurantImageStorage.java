package dev.thiagooliveira.tablesplit.domain.restaurant;

import java.util.Map;
import java.util.UUID;

public interface RestaurantImageStorage {

  String upload(RestaurantImageData image, UUID accountId, UUID restaurantId, UUID imageId);

  Map<String, Object> delete(UUID accountId, UUID restaurantId, UUID imageId);
}
