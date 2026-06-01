package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard;

import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.DashboardWidgetService;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.api.DashboardApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetsResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/dashboard")
public class DashboardApiController implements DashboardApi {

  private final DashboardWidgetService dashboardWidgetService;

  public DashboardApiController(DashboardWidgetService dashboardWidgetService) {
    this.dashboardWidgetService = dashboardWidgetService;
  }

  private AccountContext getContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new org.springframework.security.access.AccessDeniedException(
          "Access denied: User not authenticated");
    }
    return context;
  }

  @Override
  public ResponseEntity<DashboardWidgetsResponse> getDashboardWidgets() {
    var context = java.util.Objects.requireNonNull(getContext());
    var locale = LocaleContextHolder.getLocale();
    if (context.getUser() != null && context.getUser().getLanguage() != null) {
      locale = Locale.forLanguageTag(context.getUser().getLanguage().name().toLowerCase());
    }
    var response = dashboardWidgetService.getDashboard(context, locale);
    return ResponseEntity.ok(response);
  }
}
