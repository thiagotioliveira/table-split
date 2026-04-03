package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateItemCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

public class UpdateItemModel {
  private UUID id;

  @NotNull(message = "{error.menu.item.category.required}")
  private UUID categoryId;

  @NotEmpty(message = "{error.menu.item.name.required}")
  private Map<String, @NotBlank(message = "{error.menu.item.name.required}") String> name;

  @NotEmpty(message = "{error.menu.item.description.required}")
  private Map<String, @NotBlank(message = "{error.menu.item.description.required}") String>
      description;

  @NotNull(message = "{error.menu.item.price.required}")
  @Positive(message = "{error.menu.item.price.positive}")
  private BigDecimal price;

  private List<UUID> imageIdsToKeep;
  private List<MultipartFile> newImages;
  private boolean available = true;

  public CreateItemCommand toCreateItemCommand() {
    return new CreateItemCommand(
        this.categoryId,
        this.imageIdsToKeep,
        toImageCommand(),
        convertLanguages(this.name),
        convertLanguages(this.description),
        this.price,
        this.available);
  }

  public UpdateItemCommand toUpdateItemCommand() {
    return new UpdateItemCommand(
        this.categoryId,
        this.imageIdsToKeep,
        toImageCommand(),
        convertLanguages(this.name),
        convertLanguages(this.description),
        this.price,
        this.available);
  }

  private ImageCommand toImageCommand() {

    List<ImageData> images =
        newImages == null
            ? List.of()
            : newImages.stream()
                .filter(file -> !file.isEmpty())
                .map(
                    file -> {
                      try {
                        return new ImageData(
                            file.getOriginalFilename(), file.getContentType(), file.getBytes());
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    })
                .toList();

    return new ImageCommand(imageIdsToKeep == null ? List.of() : imageIdsToKeep, images);
  }

  private Map<Language, String> convertLanguages(Map<String, String> from) {
    return from.entrySet().stream()
        .collect(Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue));
  }

  public List<UUID> getImageIdsToKeep() {
    return imageIdsToKeep;
  }

  public List<MultipartFile> getNewImages() {
    return newImages;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public void setImageIdsToKeep(List<UUID> imageIdsToKeep) {
    this.imageIdsToKeep = imageIdsToKeep;
  }

  public void setNewImages(List<MultipartFile> newImages) {
    this.newImages = newImages;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}
