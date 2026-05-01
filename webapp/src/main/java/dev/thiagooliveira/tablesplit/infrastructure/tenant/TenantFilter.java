package dev.thiagooliveira.tablesplit.infrastructure.tenant;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantFilter extends OncePerRequestFilter {

  private final GetRestaurant getRestaurant;

  public TenantFilter(GetRestaurant getRestaurant) {
    this.getRestaurant = getRestaurant;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null
          && auth.isAuthenticated()
          && auth.getPrincipal() instanceof AccountContext context) {
        UUID restaurantId = context.getRestaurant().getId();
        setTenantContext(restaurantId);
      } else {
        // Handling public routes like /@{slug} or /api/v1/customer/{slug}
        String path = request.getRequestURI();
        if (path != null) {
          String slug = null;
          if (path.startsWith("/@")) {
            slug = path.substring(2).split("/")[0];
          } else if (path.startsWith("/api/v1/customer/")) {
            slug = path.substring(17).split("/")[0];
          }

          if (slug != null) {
            getRestaurant.execute(slug).ifPresent(r -> setTenantContext(r.getId()));
          }
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }

  private void setTenantContext(UUID id) {
    String tenantId = TenantContext.generateTenantIdentifier(id);
    TenantContext.setCurrentTenant(tenantId);
  }
}
