package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.utils.CurrencyMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuModel {

  private final String currencySymbol;
  private final int totalAmount;
  private final List<CategoryModel> categories;
  private final List<ItemModel> items;

  public MenuModel(List<Category> categories, List<Item> items, String currency) {
    var symbol = CurrencyMapper.symbol(currency);
    this.categories =
        categories.stream()
            .map(
                c ->
                    new CategoryModel(
                        c,
                        items.stream()
                            .filter(i -> i.getCategory().getId().equals(c.getId()))
                            .count()))
            .toList();
    this.items =
        items.stream()
            .map(
                i ->
                    new ItemModel(
                        i,
                        categories.stream()
                            .filter(c -> c.getId().equals(i.getCategory().getId()))
                            .map(c -> convertMap(c.getName()))
                            .findFirst()
                            .orElseThrow(),
                        symbol))
            .toList();
    this.totalAmount = items.size();
    this.currencySymbol = symbol;
  }

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }

  public int getTotalAmount() {
    return totalAmount;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public List<ItemModel> getItems() {
    return items;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }
}
