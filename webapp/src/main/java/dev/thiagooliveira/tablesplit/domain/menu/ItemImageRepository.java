package dev.thiagooliveira.tablesplit.domain.menu;

import java.util.UUID;

public interface ItemImageRepository {

  void save(ItemImage itemImage);

  void delete(UUID id);
}
