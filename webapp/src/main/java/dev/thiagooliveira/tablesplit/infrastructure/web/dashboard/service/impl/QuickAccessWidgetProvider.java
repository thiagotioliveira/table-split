package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.impl;

import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.service.WidgetProvider;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.DashboardWidgetResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.QuickAccessActionItem;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.spec.v1.model.QuickAccessWidgetContent;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class QuickAccessWidgetProvider implements WidgetProvider {

  private final GetItem getItem;
  private final GetCategory getCategory;
  private final org.springframework.context.MessageSource messageSource;

  public QuickAccessWidgetProvider(
      GetItem getItem,
      GetCategory getCategory,
      org.springframework.context.MessageSource messageSource) {
    this.getItem = getItem;
    this.getCategory = getCategory;
    this.messageSource = messageSource;
  }

  @Override
  public Module getRequiredModule() {
    // DASHBOARD is always active for anyone who can see the dashboard
    return Module.DASHBOARD;
  }

  @Override
  public DashboardWidgetResponse fetchWidget(AccountContext context, Locale locale) {
    var restaurantId = context.getRestaurant().getId();

    List<QuickAccessActionItem> items = new ArrayList<>();

    // Map active web modules to quick access items
    List<dev.thiagooliveira.tablesplit.infrastructure.web.Module> activeWebModules =
        new ArrayList<>();
    activeWebModules.addAll(context.getSidebarModules());
    activeWebModules.addAll(context.getFooterModules());

    for (dev.thiagooliveira.tablesplit.infrastructure.web.Module webMod : activeWebModules) {
      if (webMod == dev.thiagooliveira.tablesplit.infrastructure.web.Module.DASHBOARD) {
        continue;
      }

      QuickAccessActionItem item = new QuickAccessActionItem();
      item.setHref(webMod.getHref());
      item.setIcon(webMod.getIcon());

      switch (webMod) {
        case MENU:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.menu.title", null, "Cardápio", locale));
          long itemsCount = getItem.count(restaurantId);
          long catsCount = getCategory.count(restaurantId);
          String menuSub =
              messageSource.getMessage(
                  "dashboard.widget.quick_access.menu.description",
                  new Object[] {itemsCount, catsCount},
                  String.format("%d itens • %d categorias", itemsCount, catsCount),
                  locale);
          item.setSubtitle(menuSub);
          item.setColor("blue");
          items.add(item);
          break;
        case PROMOTIONS:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.promotions.title", null, "Promoções", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.promotions.description",
                  null,
                  "Gerencie as promoções do cardápio.",
                  locale));
          item.setColor("red");
          items.add(item);
          break;
        case ORDERS:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.orders.title", null, "Pedidos", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.orders.description",
                  null,
                  "Visualize e gerencie os pedidos.",
                  locale));
          item.setColor("green");
          items.add(item);
          break;
        case TABLES:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.tables.title", null, "Mesas", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.tables.description",
                  null,
                  "Gerencie mesas e acessos.",
                  locale));
          item.setColor("purple");
          items.add(item);
          break;
        case GALLERY:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.gallery.title", null, "Galeria", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.gallery.description",
                  null,
                  "Adicione fotos do seu restaurante.",
                  locale));
          item.setColor("pink");
          items.add(item);
          break;
        case FEEDBACKS:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.feedbacks.title", null, "Feedbacks", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.feedbacks.description",
                  null,
                  "Veja o que os clientes estão achando.",
                  locale));
          item.setColor("blue");
          items.add(item);
          break;
        case STAFF:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.staff.title", null, "Equipe", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.staff.description",
                  null,
                  "Gerencie funcionários e acessos.",
                  locale));
          item.setColor("amber");
          items.add(item);
          break;
        case REPORTS:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.reports.title", null, "Relatórios", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.reports.description",
                  null,
                  "Tenha uma visão mais detalhada.",
                  locale));
          item.setColor("blue");
          items.add(item);
          break;
        case SETTINGS:
          item.setTitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.settings.title", null, "Configurações", locale));
          item.setSubtitle(
              messageSource.getMessage(
                  "dashboard.widget.quick_access.settings.description",
                  null,
                  "Perfil e preferências.",
                  locale));
          item.setColor("teal");
          items.add(item);
          break;
        default:
          break;
      }
    }

    QuickAccessWidgetContent content = new QuickAccessWidgetContent();
    content.setActions(items);

    DashboardWidgetResponse widget = new DashboardWidgetResponse();
    widget.setId("quick_access");
    widget.setType(DashboardWidgetResponse.TypeEnum.QUICK_ACCESS);
    widget.setTitle(
        messageSource.getMessage(
            "dashboard.widget.quick_access.title", null, "Acesso Rápido", locale));
    widget.setSize(DashboardWidgetResponse.SizeEnum.LARGE);
    widget.setOrder(13);
    widget.setQuickAccessContent(content);

    return widget;
  }
}
