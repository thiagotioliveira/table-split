package dev.thiagooliveira.tablesplit.application.account;

import dev.thiagooliveira.tablesplit.domain.account.PlanLimits;
import java.util.function.Function;

public enum PlanLimitType {
  CATEGORIES(PlanLimits::categories, "error.plan.limit.categories"),
  MENU_ITEMS(PlanLimits::menuItems, "error.plan.limit.menu_items"),
  PROMOTIONS(PlanLimits::promotions, "error.plan.limit.promotions"),
  TABLES(PlanLimits::tables, "error.plan.limit.tables"),
  STAFF(PlanLimits::staff, "error.plan.limit.staff");

  private final Function<PlanLimits, Integer> limitExtractor;
  private final String errorKey;

  PlanLimitType(Function<PlanLimits, Integer> limitExtractor, String errorKey) {
    this.limitExtractor = limitExtractor;
    this.errorKey = errorKey;
  }

  public Integer getLimit(PlanLimits limits) {
    return limitExtractor.apply(limits);
  }

  public String getErrorKey() {
    return errorKey;
  }
}
