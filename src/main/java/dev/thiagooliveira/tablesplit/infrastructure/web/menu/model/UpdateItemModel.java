package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.CreateItemCommand;
import dev.thiagooliveira.tablesplit.application.menu.UpdateItemCommand;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UpdateItemModel {
  private UUID id;
  private UUID categoryId;
  private Map<String, String> name;
  private Map<String, String> description;
  private BigDecimal price;

  public CreateItemCommand toCreateItemCommand() {
    return new CreateItemCommand(
        this.categoryId,
        this.name.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.description.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.price);
  }

  public UpdateItemCommand toUpdateItemCommand() {
    return new UpdateItemCommand(
        this.categoryId,
        this.name.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.description.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.price);
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
}
