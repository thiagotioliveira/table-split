package dev.thiagooliveira.tablesplit.infrastructure.order.web.model;

import java.util.Map;
import java.util.UUID;

public class CategoryModel {
  private final String id;
  private final Map<String, String> name;

  public CategoryModel(UUID id, Map<String, String> name) {
    this.id = id.toString();
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public Map<String, String> getName() {
    return name;
  }
}
