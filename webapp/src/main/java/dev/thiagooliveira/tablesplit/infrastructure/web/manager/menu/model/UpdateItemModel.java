package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateItemCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ItemTag;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

public class UpdateItemModel {
  private UUID id;
  private UUID categoryId;
  private Map<String, String> name;
  private Map<String, String> description;
  private BigDecimal price;
  private List<String> tags;
  private boolean available;
  private List<MultipartFile> imagesFile;
  private List<String> imageIdsToKeep;
  private String questionsJson;

  private static final tools.jackson.databind.ObjectMapper objectMapper =
      new tools.jackson.databind.ObjectMapper();

  public CreateItemCommand toCreateItemCommand() {
    var imagesToKeep = parseImagesToKeep();
    var newImagesData = parseNewImages();
    var imagesCommand = new ImageCommand(imagesToKeep, newImagesData);

    return new CreateItemCommand(
        categoryId,
        imagesToKeep,
        imagesCommand,
        parseMap(name),
        parseMap(description),
        price,
        parseTags(),
        available,
        parseQuestions());
  }

  public UpdateItemCommand toUpdateItemCommand() {
    var imagesToKeep = parseImagesToKeep();
    var newImagesData = parseNewImages();
    var imagesCommand = new ImageCommand(imagesToKeep, newImagesData);

    return new UpdateItemCommand(
        id,
        categoryId,
        imagesToKeep,
        imagesCommand,
        parseMap(name),
        parseMap(description),
        price,
        parseTags(),
        available,
        parseQuestions());
  }

  private List<UUID> parseImagesToKeep() {
    return imageIdsToKeep != null
        ? imageIdsToKeep.stream()
            .filter(s -> s != null && !s.isBlank())
            .map(UUID::fromString)
            .toList()
        : List.of();
  }

  private List<ImageData> parseNewImages() {
    return imagesFile != null
        ? imagesFile.stream()
            .filter(f -> !f.isEmpty())
            .map(
                f -> {
                  try {
                    return new ImageData(f.getOriginalFilename(), f.getContentType(), f.getBytes());
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                })
            .toList()
        : List.of();
  }

  private Map<Language, String> parseMap(Map<String, String> map) {
    if (map == null) return Map.of();
    Map<Language, String> result = new HashMap<>();
    map.forEach(
        (k, v) -> {
          if (v != null && !v.isBlank()) {
            try {
              result.put(Language.valueOf(k.toUpperCase()), v);
            } catch (Exception e) {
              // Ignore invalid languages
            }
          }
        });
    return result;
  }

  private List<ItemTag> parseTags() {
    if (tags == null) return List.of();
    return tags.stream().map(ItemTag::valueOf).toList();
  }

  private Map<
          dev.thiagooliveira.tablesplit.domain.common.Language,
          List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>>
      parseQuestions() {
    if (questionsJson == null || questionsJson.isBlank()) {
      return Map.of();
    }
    try {
      // Parse as Map<String, ...> first to avoid Enum key errors
      Map<String, List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>> rawMap =
          objectMapper.readValue(
              questionsJson,
              new tools.jackson.core.type.TypeReference<
                  Map<String, List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>>>() {});

      Map<
              dev.thiagooliveira.tablesplit.domain.common.Language,
              List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>>
          result = new HashMap<>();

      rawMap.forEach(
          (langStr, list) -> {
            if (langStr == null || langStr.isBlank()) return;

            try {
              dev.thiagooliveira.tablesplit.domain.common.Language lang =
                  dev.thiagooliveira.tablesplit.domain.common.Language.valueOf(
                      langStr.toUpperCase());

              if (list != null) {
                list.forEach(
                    q -> {
                      if (q.getId() == null) q.setId(java.util.UUID.randomUUID());
                      if (q.getOptions() != null) {
                        q.getOptions()
                            .forEach(
                                opt -> {
                                  if (opt.getId() == null) opt.setId(java.util.UUID.randomUUID());
                                });
                      }
                    });
                result.put(lang, list);
              }
            } catch (IllegalArgumentException e) {
              // Ignore invalid languages like "es-ES" if they appear
            }
          });
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Erro ao processar as questões do item", e);
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public Map<String, String> getName() {
    return name;
  }

  public void setName(Map<String, String> name) {
    this.name = name;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public void setDescription(Map<String, String> description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public List<MultipartFile> getImagesFile() {
    return imagesFile;
  }

  public void setImagesFile(List<MultipartFile> imagesFile) {
    this.imagesFile = imagesFile;
  }

  public List<String> getImageIdsToKeep() {
    return imageIdsToKeep;
  }

  public void setImageIdsToKeep(List<String> imageIdsToKeep) {
    this.imageIdsToKeep = imageIdsToKeep;
  }

  public String getQuestionsJson() {
    return questionsJson;
  }

  public void setQuestionsJson(String questionsJson) {
    this.questionsJson = questionsJson;
  }
}
