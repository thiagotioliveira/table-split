package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Set;

public enum Plan {
  LITE(
      Set.of(
          Module.DASHBOARD,
          Module.MENU,
          Module.SETTINGS,
          Module.USER_PROFILE,
          Module.TABLES,
          Module.ORDERS));

  private final Set<Module> modules;

  private Plan(Set<Module> modules) {
    this.modules = modules;
  }

  public Set<Module> getModules() {
    return modules;
  }
}
