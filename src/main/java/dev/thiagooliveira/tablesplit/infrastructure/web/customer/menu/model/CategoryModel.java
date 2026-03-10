package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryModel {
  private final UUID id;
  private final Map<String, String> name;
  private final List<ItemModel> items = new ArrayList<>();
  private final int itemsSize;

  public CategoryModel(Category category, List<Item> items, String symbol) {
    this.id = category.getId();
    this.name = convertMap(category.getName());
    items.stream()
        .filter(i -> i.getCategoryId().equals(category.getId()))
        .forEach(i -> this.items.add(new ItemModel(i, symbol)));
    this.itemsSize = this.items.size();
  }

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getName() {
    return name;
  }

  public List<ItemModel> getItems() {
    return items;
  }

  public int getItemsSize() {
    return itemsSize;
  }
}
