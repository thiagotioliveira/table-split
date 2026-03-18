package dev.thiagooliveira.tablesplit.infrastructure.web;

import dev.thiagooliveira.tablesplit.domain.account.Plan;
import java.util.List;

public enum Module {
  DASHBOARD(
      true,
      false,
      "dashboard",
      1,
      "nav.dashboard",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect width=\"7\" height=\"7\" x=\"3\" y=\"3\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"14\" y=\"3\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"14\" y=\"14\" rx=\"1\"/><rect width=\"7\" height=\"7\" x=\"3\" y=\"14\" rx=\"1\"/></svg>"),
  MENU(
      true,
      false,
      "menu",
      2,
      "nav.menu",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M17 21a1 1 0 0 0 1-1v-5.35c0-.457.316-.844.727-1.041a4 4 0 0 0-2.134-7.589 5 5 0 0 0-9.186 0 4 4 0 0 0-2.134 7.588c.411.198.727.585.727 1.041V20a1 1 0 0 0 1 1Z\"/><path d=\"m6 17 11-1\"/></svg>"),
  SETTINGS(
      true,
      false,
      "settings",
      10,
      "nav.settings",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/></svg>"),
  USER_PROFILE(
      false,
      true,
      "profile",
      1,
      "nav.user.profile",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path d=\"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2\"/><circle cx=\"12\" cy=\"7\" r=\"4\"/></svg>");

  private final boolean inSidebarNav;
  private final boolean inSidebarFooter;
  private final String view;
  private final String name;
  private final String icon;
  private final Integer index;

  private Module(
      boolean inSidebarNav,
      boolean inSidebarFooter,
      String view,
      int index,
      String name,
      String icon) {
    this.inSidebarNav = inSidebarNav;
    this.inSidebarFooter = inSidebarFooter;
    this.view = view;
    this.index = index;
    this.name = name;
    this.icon = icon;
  }

  public static List<Module> sidebarModules(Plan plan) {
    return plan.getModules().stream()
        .map(m -> Module.valueOf(m.name()))
        .filter(Module::isInSidebarNav)
        .sorted((o1, o2) -> o1.index.compareTo(o2.index))
        .toList();
  }

  public static List<Module> footerModules(Plan plan) {
    return plan.getModules().stream()
        .map(m -> Module.valueOf(m.name()))
        .filter(Module::isInSidebarFooter)
        .sorted((o1, o2) -> o1.index.compareTo(o2.index))
        .toList();
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
