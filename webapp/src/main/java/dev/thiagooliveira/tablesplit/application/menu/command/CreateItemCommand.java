package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion;
import dev.thiagooliveira.tablesplit.domain.menu.ItemTag;
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
    List<ItemTag> tags,
    boolean available,
    Map<Language, List<ItemQuestion>> questions) {

  // Used by tests and DemoData
  public CreateItemCommand(
      UUID categoryId,
      List<UUID> imageIdsToKeep,
      ImageCommand images,
      Map<Language, String> name,
      Map<Language, String> description,
      BigDecimal price,
      List<ItemTag> tags,
      boolean available) {
    this(categoryId, imageIdsToKeep, images, name, description, price, tags, available, Map.of());
  }

  // Used by DemoData (implicit available=true)
  public CreateItemCommand(
      UUID categoryId,
      List<UUID> imageIdsToKeep,
      ImageCommand images,
      Map<Language, String> name,
      Map<Language, String> description,
      BigDecimal price,
      List<ItemTag> tags) {
    this(categoryId, imageIdsToKeep, images, name, description, price, tags, true, Map.of());
  }
}
