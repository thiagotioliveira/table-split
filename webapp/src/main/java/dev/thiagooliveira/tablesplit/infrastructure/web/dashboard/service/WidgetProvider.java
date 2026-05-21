package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service;

import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.Locale;

public interface WidgetProvider {
  Module getRequiredModule();

  DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale);
}
