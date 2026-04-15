package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import java.math.BigDecimal;

public record PromotionItemModel(
    String id, String name, BigDecimal price, String category, String image) {
  public static PromotionItemModel from(Item item, Language language) {
    String nameStr = item.getName().get(language);
    String categoryStr =
        item.getCategory() != null ? item.getCategory().getName().get(language) : "Geral";
    String imageStr =
        (item.getImages() != null && !item.getImages().isEmpty())
            ? item.getImages().get(0).getName()
            : "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100";

    return new PromotionItemModel(
        item.getId().toString(), nameStr, item.getPrice(), categoryStr, imageStr);
  }
}
