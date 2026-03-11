package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

  Optional<Item> findById(UUID id);

  List<Item> findAll(UUID restaurantId);

  Item save(Item item);

  void delete(UUID itemId);

  long count();

  long countActive();

  long countInactive();
}
