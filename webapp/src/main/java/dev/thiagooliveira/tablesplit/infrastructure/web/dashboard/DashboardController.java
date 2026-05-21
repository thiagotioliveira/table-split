package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.DASHBOARD)
@RequestMapping("/dashboard")
public class DashboardController {

  @GetMapping
  public String index() {
    return "dashboard";
  }
}
