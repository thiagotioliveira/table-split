package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryModel {
  private final String id;
  private final int order;
  private final Map<String, String> name;
  private final long amount;

  public CategoryModel(Category category, long amount) {
    this.id = category.getId().toString();
    this.order = category.getOrder();
    this.name = convertMap(category.getName());
    this.amount = amount;
  }

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }

  public String getId() {
    return id;
  }

  public int getOrder() {
    return order;
  }

  public Map<String, String> getName() {
    return name;
  }

  public long getAmount() {
    return amount;
  }
}
