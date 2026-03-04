package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.dto.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.application.menu.dto.UpdateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UpdateCategoryModel {
  private UUID id;
  private Integer order;
  private Map<String, String> name;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Map<String, String> getName() {
    return name;
  }

  public void setName(Map<String, String> name) {
    this.name = name;
  }

  public UpdateCategoryCommand toUpdateCategoryCommand() {
    return new UpdateCategoryCommand(
        this.name.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.order == null ? 1 : this.order);
  }

  public CreateCategoryCommand toCreateCategoryCommand() {
    return new CreateCategoryCommand(
        this.name.entrySet().stream()
            .collect(
                Collectors.toMap(entry -> Language.valueOf(entry.getKey()), Map.Entry::getValue)),
        this.order == null ? 1 : this.order);
  }
}
