package dev.thiagooliveira.tablesplit.infrastructure.web;

public enum ItemTag {
  VEGETARIAN("item.tag.vegetarian", "🥦"),
  VEGAN("item.tag.vegan", "🌿"),
  GLUTEN_FREE("item.tag.gluten_free", "🌾"),
  LACTOSE_FREE("item.tag.lactose_free", "🥛"),
  NUT_FREE("item.tag.nut_free", "🥜"),
  SPICY("item.tag.spicy", "🌶️"),
  NEW("item.tag.new", "✨"),
  POPULAR("item.tag.popular", "🔥"),
  CHEF_RECOMMENDATION("item.tag.chef_recommendation", "⭐");

  private final String label;
  private final String icon;

  ItemTag(String label, String icon) {
    this.label = label;
    this.icon = icon;
  }

  public static ItemTag fromDomain(dev.thiagooliveira.tablesplit.domain.menu.ItemTag domain) {
    return valueOf(domain.name());
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }
}
