package dev.thiagooliveira.tablesplit.infrastructure.menu.api;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.CategoriesApi;
import dev.thiagooliveira.tablesplit.infrastructure.menu.api.spec.v1.model.CategoryResponse;
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
public class CategoriesApiController implements CategoriesApi {

  private final GetCategory getCategory;
  private final MenuApiMapper mapper;

  public CategoriesApiController(GetCategory getCategory, MenuApiMapper mapper) {
    this.getCategory = getCategory;
    this.mapper = mapper;
  }

  private UUID getRestaurantId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AccountContext context = (AccountContext) auth.getPrincipal();
    return context.getRestaurant().getId();
  }

  @Override
  public ResponseEntity<List<CategoryResponse>> getCategories(List<String> languages) {
    var restaurantId = getRestaurantId();
    var languageList =
        languages == null
            ? List.of(Language.PT)
            : languages.stream().map(Language::valueOf).toList();
    var displayLanguage = languageList.get(0);

    var categories = getCategory.execute(restaurantId, languageList);
    return ResponseEntity.ok(
        categories.stream().map(c -> mapper.toCategoryResponse(c, displayLanguage)).toList());
  }
}
