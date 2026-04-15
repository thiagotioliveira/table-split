package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateItemCommand(
    UUID categoryId,
    List<UUID> imageIdsToKeep,
    ImageCommand images,
    Map<Language, String> name,
    Map<Language, String> description,
    BigDecimal price,
    List<dev.thiagooliveira.tablesplit.domain.menu.ItemTag> tags,
    boolean available) {

  public CreateItemCommand(
      UUID categoryId,
      List<UUID> imageIdsToKeep,
      ImageCommand images,
      Map<Language, String> name,
      Map<Language, String> description,
      BigDecimal price,
      List<dev.thiagooliveira.tablesplit.domain.menu.ItemTag> tags) {
    this(categoryId, imageIdsToKeep, images, name, description, price, tags, true);
  }
}
