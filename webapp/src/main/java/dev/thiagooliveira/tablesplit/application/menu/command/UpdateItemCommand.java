package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateItemCommand(
    UUID categoryId,
    List<UUID> imageIdsToKeep,
    ImageCommand images,
    Map<Language, String> name,
    Map<Language, String> description,
    BigDecimal price,
    List<dev.thiagooliveira.tablesplit.domain.menu.ItemTag> tags,
    boolean available) {

  public UpdateItemCommand(
      UUID categoryId,
      List<UUID> imageIdsToKeep,
      ImageCommand images,
      Map<Language, String> name,
      Map<Language, String> description,
      BigDecimal price) {
    this(categoryId, imageIdsToKeep, images, name, description, price, List.of(), true);
  }
}
