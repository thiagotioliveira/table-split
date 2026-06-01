package dev.thiagooliveira.tablesplit.infrastructure.web.security.context;

import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerContextModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Centralizes the extraction of session context objects used by manager-facing controllers.
 * Eliminates duplicated guard logic across controllers.
 */
@Component
public class AccountContextResolver {

  /**
   * Resolves the {@link AccountContext} from the given {@link Authentication}.
   *
   * @param auth the current Spring Security authentication
   * @return the authenticated {@link AccountContext}
   * @throws AccessDeniedException if the authentication is null or the principal is not an {@link
   *     AccountContext}
   */
  public AccountContext resolve(Authentication auth) {
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new AccessDeniedException("Access denied: User not authenticated");
    }
    return context;
  }

  /**
   * Resolves the {@link ManagerContextModel} from the given Spring MVC {@link Model}.
   *
   * <p>This pattern is injected by the security interceptor before each controller method is
   * invoked, so the model attribute {@code "context"} must always be present and valid.
   *
   * @param model the Spring MVC model
   * @return the resolved {@link ManagerContextModel}
   * @throws AccessDeniedException if the context attribute is missing or contains no restaurant
   */
  public ManagerContextModel resolve(Model model) {
    var context = (ManagerContextModel) model.getAttribute("context");
    if (context == null || context.getRestaurant() == null) {
      throw new AccessDeniedException("Access denied: Invalid session context");
    }
    return context;
  }
}
