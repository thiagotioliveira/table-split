package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

  Optional<Item> getById(UUID id);

  List<Item> getAll(UUID restaurantId);

  void save(Item item);

  void delete(UUID itemId);
}
