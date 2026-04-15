package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateCategoryCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UpdateCategoryModel {
  private UUID id;

  @NotNull(message = "{error.menu.category.order.required}")
  private Integer order;

  @NotEmpty(message = "{error.menu.category.name.required}")
  private Map<String, @NotBlank(message = "{error.menu.category.name.required}") String> name;

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
