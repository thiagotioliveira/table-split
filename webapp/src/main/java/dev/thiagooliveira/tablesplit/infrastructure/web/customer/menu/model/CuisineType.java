package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

public enum CuisineType {
  CONTEMPORARY("cuisine.type.contemporary", "🍽️"),
  BRAZILIAN("cuisine.type.brazilian", "🍖"),
  ITALIAN("cuisine.type.italian", "🍝"),
  MEXICAN("cuisine.type.mexican", "🌮"),
  FAST_FOOD("cuisine.type.fastfood", "🍟"),
  VEGETARIAN("cuisine.type.vegetarian", "🥗"),
  JAPANESE("cuisine.type.japanese", "🍣"),
  FRENCH("cuisine.type.french", "🥐"),
  CHINESE("cuisine.type.chinese", "🥡"),
  INDIANA("cuisine.type.indian", "🍛"),
  ARABIC("cuisine.type.arabic", "🧆"),
  AMERICAN("cuisine.type.american", "🍔"),
  PIZZERIA("cuisine.type.pizzeria", "🍕"),
  SEAFOOD("cuisine.type.seafood", "🦐"),
  STEAKHOUSE("cuisine.type.steakhouse", "🥩"),
  CAFETERIA("cuisine.type.cafeteria", "☕"),
  SWEET_SHOP("cuisine.type.sweetshop", "🍰");
  private final String label;
  private final String icon;

  private CuisineType(String label, String icon) {
    this.label = label;
    this.icon = icon;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }
}
