package dev.thiagooliveira.tablesplit.infrastructure.web.menu.model;

import dev.thiagooliveira.tablesplit.domain.menu.Category;
import java.util.List;

public class MenuModel {

  private int totalAmount = 0;
  private List<CategoryModel> categories;

  public MenuModel(List<Category> categories) {
    this.categories = categories.stream().map(c -> new CategoryModel(c, 0)).toList();
  }

  public int getTotalAmount() {
    return totalAmount;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }
}
