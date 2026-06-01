package dev.thiagooliveira.tablesplit.infrastructure.report.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.REPORTS)
@RequestMapping("/reports")
public class ReportController {

  @GetMapping
  public String index(
      org.springframework.security.core.Authentication auth, org.springframework.ui.Model model) {
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new org.springframework.security.access.AccessDeniedException(
          "Access denied: User not authenticated");
    }
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    return "reports";
  }
}
