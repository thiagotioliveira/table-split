package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Set;

public enum Plan {
  LITE(
      Set.of(
          Module.DASHBOARD,
          Module.MENU,
          Module.SETTINGS,
          Module.USER_PROFILE,
          Module.ACCOUNT,
          Module.GALLERY,
          Module.PROMOTIONS)),
  PRO(
      Set.of(
          Module.DASHBOARD,
          Module.MENU,
          Module.SETTINGS,
          Module.USER_PROFILE,
          Module.ACCOUNT,
          Module.TABLES,
          Module.ORDERS,
          Module.GALLERY,
          Module.RESERVATION,
          Module.REPORTS,
          Module.STAFF,
          Module.PROMOTIONS));

  private final Set<Module> modules;

  private Plan(Set<Module> modules) {
    this.modules = modules;
  }

  public Set<Module> getModules() {
    return modules;
  }
}
