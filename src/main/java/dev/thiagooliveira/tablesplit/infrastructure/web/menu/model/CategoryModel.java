package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryModel {
  private final UUID id;
  private final int order;
  private final Map<String, String> name;
  private final int amount;

  public CategoryModel(Category category, int amount) {
    this.id = category.getId();
    this.order = category.getOrder();
    this.name =
        category.getName().entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
    this.amount = amount;
  }

  public UUID getId() {
    return id;
  }

  public int getOrder() {
    return order;
  }

  public Map<String, String> getName() {
    return name;
  }

  public int getAmount() {
    return amount;
  }
}
