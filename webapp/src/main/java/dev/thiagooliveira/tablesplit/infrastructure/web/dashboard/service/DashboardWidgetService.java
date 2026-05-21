package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service;

import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetsResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.RestaurantCardResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DashboardWidgetService {

  private static final Logger log = LoggerFactory.getLogger(DashboardWidgetService.class);

  private final List<WidgetProvider> providers;
  private final Executor dashboardExecutor;
  private final org.springframework.context.MessageSource messageSource;

  public DashboardWidgetService(
      List<WidgetProvider> providers,
      @Qualifier("dashboardExecutor") Executor dashboardExecutor,
      org.springframework.context.MessageSource messageSource) {
    this.providers = providers;
    this.dashboardExecutor = dashboardExecutor;
    this.messageSource = messageSource;
  }

  public DashboardWidgetsResponse getDashboard(AccountContext context, Locale locale) {
    Set<dev.thiagooliveira.tablesplit.domain.account.Module> activeModules = new HashSet<>();
    activeModules.add(dev.thiagooliveira.tablesplit.domain.account.Module.DASHBOARD);
    activeModules.add(dev.thiagooliveira.tablesplit.domain.account.Module.SETTINGS);

    if (context.getSidebarModules() != null) {
      context
          .getSidebarModules()
          .forEach(
              m -> {
                try {
                  activeModules.add(
                      dev.thiagooliveira.tablesplit.domain.account.Module.valueOf(m.name()));
                } catch (Exception e) {
                  // ignore mapping differences
                }
              });
    }

    if (context.getFooterModules() != null) {
      context
          .getFooterModules()
          .forEach(
              m -> {
                try {
                  activeModules.add(
                      dev.thiagooliveira.tablesplit.domain.account.Module.valueOf(m.name()));
                } catch (Exception e) {
                  // ignore mapping differences
                }
              });
    }

    List<CompletableFuture<DashboardWidgetResponse>> futures = new ArrayList<>();
    String tenantId =
        dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.getCurrentTenant();

    for (WidgetProvider provider : providers) {
      if (activeModules.contains(provider.getRequiredModule())) {
        try {
          CompletableFuture<DashboardWidgetResponse> future =
              CompletableFuture.supplyAsync(
                      () -> {
                        try {
                          dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext
                              .setCurrentTenant(tenantId);
                          return provider.fetchWidget(context, locale);
                        } finally {
                          dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.clear();
                        }
                      },
                      dashboardExecutor)
                  .exceptionally(
                      throwable -> {
                        log.error(
                            "Error executing widget provider: {}",
                            provider.getClass().getSimpleName(),
                            throwable);
                        return null;
                      });
          futures.add(future);
        } catch (Exception e) {
          log.error(
              "Failed to submit async task for widget provider: {}. Error: {}",
              provider.getClass().getSimpleName(),
              e.getMessage(),
              e);
        }
      }
    }

    // Wait for all futures to complete with a resilient timeout of 3 seconds
    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(3, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.warn(
          "Some dashboard widgets execution timed out or failed to complete: {}", e.getMessage());
    }

    List<DashboardWidgetResponse> widgets =
        futures.stream()
            .map(
                f -> {
                  try {
                    return f.getNow(null);
                  } catch (Exception e) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(DashboardWidgetResponse::getOrder))
            .toList();

    RestaurantCardResponse restaurantSummary = new RestaurantCardResponse();
    restaurantSummary.setName(context.getRestaurant().getName());
    restaurantSummary.setAddress(context.getRestaurant().getAddress());
    restaurantSummary.setCurrencySymbol(context.getRestaurant().getCurrency().getSymbol());
    restaurantSummary.setPublicProfileUrl("/@" + context.getRestaurant().getSlug());

    String welcomeMessage =
        messageSource.getMessage(
            "dashboard.welcome.title",
            new Object[] {context.getUser().getFirstName()},
            "Olá, " + context.getUser().getFirstName() + "! 👋",
            locale);

    String welcomeHint =
        messageSource.getMessage(
            "dashboard.welcome.subtitle",
            null,
            "Gerencie seu cardápio e configurações do restaurante.",
            locale);

    DashboardWidgetsResponse response = new DashboardWidgetsResponse();
    response.setRestaurantCard(restaurantSummary);
    response.setWelcomeMessage(welcomeMessage);
    response.setWelcomeHint(welcomeHint);
    response.setWidgets(widgets);

    return response;
  }
}
