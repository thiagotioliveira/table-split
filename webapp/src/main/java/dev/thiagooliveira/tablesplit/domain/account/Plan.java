package dev.thiagooliveira.tablesplit.domain.account;

import java.util.Set;

public enum Plan {
  STARTER(
      new PlanLimits(6, 40, 10, 5, 0, 0, -1),
      Set.of(
          Module.DASHBOARD,
          Module.MENU,
          Module.SETTINGS,
          Module.USER_PROFILE,
          Module.ACCOUNT,
          Module.GALLERY,
          Module.PROMOTIONS)),
  PROFESSIONAL(
      new PlanLimits(15, 100, 30, 15, 15, 5, 90),
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
          Module.PROMOTIONS)),
  ENTERPRISE(
      PlanLimits.UNLIMITED,
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

  private final PlanLimits limits;
  private final Set<Module> modules;

  private Plan(PlanLimits limits, Set<Module> modules) {
    this.limits = limits;
    this.modules = modules;
  }

  public PlanLimits getLimits() {
    return limits;
  }

  public Set<Module> getModules() {
    return modules;
  }
}
