package dev.thiagooliveira.tablesplit.application.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemImage;
import java.util.UUID;

public interface ItemImageRepository {

  void save(ItemImage itemImage);

  void delete(UUID id);
}
