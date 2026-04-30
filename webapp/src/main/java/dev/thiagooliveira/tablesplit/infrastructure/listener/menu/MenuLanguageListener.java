package dev.thiagooliveira.tablesplit.infrastructure.listener.menu;

import dev.thiagooliveira.tablesplit.application.menu.CategoryRepository;
import dev.thiagooliveira.tablesplit.application.menu.ItemRepository;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantUpdatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantOperationService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MenuLanguageListener {

  private static final Logger logger = LoggerFactory.getLogger(MenuLanguageListener.class);

  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;
  private final TenantOperationService tenantOperationService;

  public MenuLanguageListener(
      ItemRepository itemRepository,
      CategoryRepository categoryRepository,
      TenantOperationService tenantOperationService) {
    this.itemRepository = itemRepository;
    this.categoryRepository = categoryRepository;
    this.tenantOperationService = tenantOperationService;
  }

  @EventListener
  public void onRestaurantUpdated(RestaurantUpdatedEvent event) {
    if (event.getAddedLanguages().isEmpty() && event.getRemovedLanguages().isEmpty()) {
      return;
    }

    UUID restaurantId = event.getRestaurantId();
    String tenantId = TenantContext.generateTenantIdentifier(restaurantId);
    String originalTenant = TenantContext.getCurrentTenant();
    TenantContext.setCurrentTenant(tenantId);
    dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.setAccountId(
        restaurantId, event.getAccountId());

    try {
      this.tenantOperationService.runInNewTransaction(
          () -> {
            var defaultLanguage = event.getDefaultLanguage();

            var categories = categoryRepository.findByRestaurantId(restaurantId);
            for (var category : categories) {
              boolean changed = false;
              for (var addedLang : event.getAddedLanguages()) {
                if (category.getName() != null) {
                  var defaultVal = category.getName().getOrDefault(defaultLanguage, "");
                  category.getName().put(addedLang, defaultVal);
                  changed = true;
                }
              }
              for (var removedLang : event.getRemovedLanguages()) {
                if (category.getName() != null) {
                  category.getName().remove(removedLang);
                  changed = true;
                }
              }
              if (changed) {
                categoryRepository.save(category);
              }
            }

            var items = itemRepository.findByRestaurantId(restaurantId);
            for (var item : items) {
              boolean changed = false;
              for (var addedLang : event.getAddedLanguages()) {
                if (item.getName() != null) {
                  var defaultName = item.getName().getOrDefault(defaultLanguage, "");
                  item.getName().put(addedLang, defaultName);
                  changed = true;
                }
                if (item.getDescription() != null) {
                  var defaultDescription = item.getDescription().getOrDefault(defaultLanguage, "");
                  item.getDescription().put(addedLang, defaultDescription);
                  changed = true;
                }
              }
              for (var removedLang : event.getRemovedLanguages()) {
                if (item.getName() != null) {
                  item.getName().remove(removedLang);
                  changed = true;
                }
                if (item.getDescription() != null) {
                  item.getDescription().remove(removedLang);
                  changed = true;
                }
              }
              if (changed) {
                itemRepository.save(item);
              }
            }
          });
    } catch (Exception e) {
      logger.error(
          "[MenuLanguageListener] Error updating menu languages for restaurant: {}",
          restaurantId,
          e);
      throw e;
    } finally {
      dev.thiagooliveira.tablesplit.infrastructure.tenant.AccountIdContext.clear();
      if (originalTenant != null) {
        TenantContext.setCurrentTenant(originalTenant);
      } else {
        TenantContext.clear();
      }
    }
  }
}
