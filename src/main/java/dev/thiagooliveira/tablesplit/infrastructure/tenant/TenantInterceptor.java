package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/** Interceptor that resolves the current tenant based on the 'slug' path variable. */
@Component
public class TenantInterceptor implements HandlerInterceptor {

  private final GetRestaurant getRestaurant;

  public TenantInterceptor(GetRestaurant getRestaurant) {
    this.getRestaurant = getRestaurant;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    @SuppressWarnings("unchecked")
    Map<String, String> pathVariables =
        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

    if (pathVariables != null && pathVariables.containsKey("slug")) {
      String slug = pathVariables.get("slug");
      // Remove the '@' prefix if present
      if (slug.startsWith("@")) {
        slug = slug.substring(1);
      }

      Optional<Restaurant> restaurant = getRestaurant.execute(slug);
      if (restaurant.isPresent()) {
        String tenantId =
            "t_" + restaurant.get().getId().toString().replace("-", "_").toLowerCase();
        TenantContext.setCurrentTenant(tenantId);
      }
    } else {
      // Default to public schema for non-tenant requests
      TenantContext.setCurrentTenant(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
    }

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    TenantContext.clear();
  }
}
