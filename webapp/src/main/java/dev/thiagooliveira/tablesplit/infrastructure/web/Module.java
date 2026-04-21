package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.account.Plan;
import java.util.List;
import java.util.Set;

public enum Module {
  DASHBOARD(
      true,
      true,
      false,
      "dashboard",
      1,
      "nav.dashboard",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"7\" height=\"7\" x=\"3\" y=\"3\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"14\" y=\"3\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"14\" y=\"14\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"3\" y=\"14\" rx=\"1\"/></svg>"),
  MENU(
      true,
      true,
      false,
      "menu",
      2,
      "nav.menu",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M17 21a1 1 0 0 0 1-1v-5.35c0-.457.316-.844.727-1.041a4 4 0 0 0-2.134-7.589 5 5 0 0 0-9.186 0 4 4 0 0 0-2.134 7.588c.411.198.727.585.727 1.041V20a1 1 0 0 0 1 1Z\"/><path d=\"m6 17 11-1\"/></svg>"),
  PROMOTIONS(
      true,
      true,
      false,
      "promotions",
      3,
      "nav.promotions",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M3.85 8.62a4 4 0 0 1 4.78-4.77 4 4 0 0 1 6.74 0 4 4 0 0 1 4.78 4.78 4 4 0 0 1 0 6.74 4 4 0 0 1-4.77 4.78 4 4 0 0 1-6.75 0 4 4 0 0 1-4.78-4.77 4 4 0 0 1 0-6.76Z\"></path><path d=\"m9 12 2 2 4-4\"></path></svg>"),
  USER_PROFILE(
      true,
      false,
      true,
      "profile",
      2,
      "nav.user.profile",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2\"/><circle cx=\"12\" cy=\"7\" r=\"4\"/></svg>"),
  ACCOUNT(
      true,
      false,
      true,
      "account",
      1,
      "nav.account",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"20\" height=\"14\" x=\"2\" y=\"5\" rx=\"2\"></rect><line x1=\"2\" x2=\"22\" y1=\"10\" y2=\"10\"></line></svg>"),
  TABLES(
      true,
      true,
      false,
      "tables",
      4,
      "nav.tables",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"18\" height=\"18\" x=\"3\" y=\"3\" rx=\"2\"></rect><path d=\"M7 7h.01\"></path><path d=\"M17 7h.01\"></path><path d=\"M7 17h.01\"></path><path d=\"M17 17h.01\"></path></svg>"),
  ORDERS(
      true,
      true,
      false,
      "orders",
      5,
      "nav.orders",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2\"></path><path d=\"M15 2H9a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1Z\"></path><path d=\"M12 11h4\"></path><path d=\"M12 16h4\"></path><path d=\"M8 11h.01\"></path><path d=\"M8 16h.01\"></path></svg>"),
  FEEDBACKS(
      true,
      true,
      false,
      "feedbacks",
      6,
      "nav.feedbacks",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z\"></path><path d=\"M12 7v2\"></path><path d=\"M12 13h.01\"></path></svg>"),
  RESERVATION(
      false,
      true,
      false,
      "reservations",
      6,
      "nav.reservation",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"18\" height=\"18\" x=\"3\" y=\"4\" rx=\"2\" ry=\"2\"></rect><line x1=\"16\" x2=\"16\" y1=\"2\" y2=\"6\"></line><line x1=\"8\" x2=\"8\" y1=\"2\" y2=\"6\"></line><line x1=\"3\" x2=\"21\" y1=\"10\" y2=\"10\"></line></svg>"),
  GALLERY(
      true,
      true,
      false,
      "gallery",
      7,
      "nav.gallery",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"18\" height=\"18\" x=\"3\" y=\"3\" rx=\"2\" ry=\"2\"></rect><circle cx=\"9\" cy=\"9\" r=\"2\"></circle><path d=\"m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21\"></path></svg>"),
  REPORTS(
      true,
      true,
      false,
      "reports",
      8,
      "nav.reports",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M3 3v18h18\"></path><path d=\"m19 9-5 5-4-4-3 3\"></path></svg>"),
  STAFF(
      true,
      true,
      false,
      "staff",
      9,
      "nav.staff",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2\"></path><circle cx=\"9\" cy=\"7\" r=\"4\"></circle><path d=\"M22 21v-2a4 4 0 0 0-3-3.87\"></path><path d=\"M16 3.13a4 4 0 0 1 0 7.75\"></path></svg>"),
  SETTINGS(
      true,
      true,
      false,
      "settings",
      10,
      "nav.settings",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/></svg>");

  private final boolean active;
  private final boolean inSidebarNav;
  private final boolean inSidebarFooter;
  private final String view;
  private final String name;
  private final String icon;
  private final Integer index;

  private Module(
      boolean active,
      boolean inSidebarNav,
      boolean inSidebarFooter,
      String view,
      int index,
      String name,
      String icon) {
    this.active = active;
    this.inSidebarNav = inSidebarNav;
    this.inSidebarFooter = inSidebarFooter;
    this.view = view;
    this.index = index;
    this.name = name;
    this.icon = icon;
  }

  public static Set<Module> staffAvailableModules() {
    return Set.of(MENU, PROMOTIONS, TABLES, ORDERS, GALLERY, REPORTS);
  }

  public static List<Module> sidebarModules(Plan plan) {
    return plan.getModules().stream()
        .map(m -> Module.valueOf(m.name()))
        .filter(m -> m.active && m.inSidebarNav)
        .sorted((o1, o2) -> o1.index.compareTo(o2.index))
        .toList();
  }

  public static List<Module> footerModules(Plan plan) {
    return plan.getModules().stream()
        .map(m -> Module.valueOf(m.name()))
        .filter(m -> m.active && m.inSidebarFooter)
        .sorted((o1, o2) -> o1.index.compareTo(o2.index))
        .toList();
  }

  public static List<Module> filterModules(
      java.util.Set<dev.thiagooliveira.tablesplit.domain.account.Module> modules, boolean sidebar) {
    return modules.stream()
        .map(m -> Module.valueOf(m.name()))
        .filter(m -> m.active && (sidebar ? m.inSidebarNav : m.inSidebarFooter))
        .sorted((o1, o2) -> o1.index.compareTo(o2.index))
        .toList();
  }

  public boolean isActive() {
    return active;
  }

  public boolean isInSidebarNav() {
    return inSidebarNav;
  }

  public boolean isInSidebarFooter() {
    return inSidebarFooter;
  }

  public String getView() {
    return view;
  }

  public String getHref() {
    return String.format("/%s", this.view);
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public int getIndex() {
    return index;
  }
}
