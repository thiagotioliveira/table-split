package dev.thiagooliveira.tablesplit.infrastructure.menu.api;

import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.ItemsApi;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.ItemResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
public class ItemsApiController implements ItemsApi {

  private final GetItem getItem;
  private final MenuApiMapper mapper;

  public ItemsApiController(GetItem getItem, MenuApiMapper mapper) {
    this.getItem = getItem;
    this.mapper = mapper;
  }

  private UUID getRestaurantId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new org.springframework.security.access.AccessDeniedException(
          "Access denied: User not authenticated");
    }
    return context.getRestaurant().getId();
  }

  @Override
  public ResponseEntity<List<ItemResponse>> getItems(
      List<String> languages, Boolean includePromotions) {
    var restaurantId = getRestaurantId();
    var languageList =
        languages == null
            ? List.of(Language.PT)
            : languages.stream().map(Language::valueOf).toList();
    var displayLanguage = languageList.get(0);

    var items = getItem.execute(restaurantId, languageList, Boolean.TRUE.equals(includePromotions));
    return ResponseEntity.ok(
        items.stream().map(i -> mapper.toItemResponse(i, displayLanguage)).toList());
  }
}
