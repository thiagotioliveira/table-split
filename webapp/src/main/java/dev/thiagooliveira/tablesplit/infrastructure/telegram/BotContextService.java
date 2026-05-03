package dev.thiagooliveira.tablesplit.infrastructure.telegram;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Category;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BotContextService {

  private final GetRestaurant getRestaurant;
  private final GetCategory getCategory;
  private final GetItem getItem;
  private final dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext
      transactionalContext;

  public BotContextService(
      GetRestaurant getRestaurant,
      GetCategory getCategory,
      GetItem getItem,
      dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext
          transactionalContext) {
    this.getRestaurant = getRestaurant;
    this.getCategory = getCategory;
    this.getItem = getItem;
    this.transactionalContext = transactionalContext;
  }

  public String getRestaurantContext(UUID restaurantId) {
    TenantContext.setCurrentTenant(TenantContext.generateTenantIdentifier(restaurantId));
    try {
      return transactionalContext.execute(
          () -> {
            Restaurant restaurant = getRestaurant.execute(restaurantId).orElse(null);
            if (restaurant == null) return "Restaurante não encontrado.";

            List<Language> langs = List.of(Language.PT);
            List<Category> categories = getCategory.execute(restaurantId, langs);
            List<Item> items = getItem.execute(restaurantId, langs, true);

            StringBuilder sb = new StringBuilder();
            sb.append("Informações do Restaurante:\n");
            sb.append("Nome: ").append(restaurant.getName()).append("\n");
            sb.append("Descrição: ").append(restaurant.getDescription()).append("\n");
            sb.append("Cozinha: ").append(restaurant.getCuisineType()).append("\n");
            sb.append("\nCardápio Atual:\n");

            for (Category cat : categories) {
              String catName =
                  cat.getName() != null
                      ? cat.getName().getOrDefault(Language.PT, "Categoria")
                      : "Categoria";
              sb.append("--- ").append(catName).append(" ---\n");
              List<Item> catItems =
                  items.stream()
                      .filter(
                          i ->
                              i.getCategory() != null
                                  && i.getCategory().getId().equals(cat.getId()))
                      .filter(Item::isAvailable)
                      .collect(Collectors.toList());

              for (Item item : catItems) {
                String name =
                    item.getName() != null
                        ? item.getName().getOrDefault(Language.PT, "Item")
                        : "Item";
                String desc =
                    item.getDescription() != null
                        ? item.getDescription().getOrDefault(Language.PT, "")
                        : "";

                sb.append("- ").append(name).append(": €").append(item.getPrice());
                if (item.getPromotion() != null) {
                  sb.append(" (PROMOÇÃO: €")
                      .append(item.getPromotion().promotionalPrice())
                      .append(")");
                }
                sb.append("\n  Descrição: ").append(desc).append("\n");
              }
              sb.append("\n");
            }

            return sb.toString();
          });
    } finally {
      TenantContext.setCurrentTenant(null);
    }
  }
}
