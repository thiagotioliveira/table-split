package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

  Optional<Item> findById(UUID id);

  Optional<Item> findByIdIncludingDeleted(UUID id);

  List<Item> findByRestaurantId(UUID restaurantId);

  List<Item> findAll(UUID restaurantId, List<Language> languages);

  Item save(Item item);

  void delete(UUID itemId);

  boolean existsInTicketItems(UUID itemId);

  long count(UUID restaurantId);

  long countActive(UUID restaurantId);

  long countInactive(UUID restaurantId);
}
