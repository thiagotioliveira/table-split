package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.GetDashboard;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

  private final GetDashboard getDashboard;

  public DashboardController(GetDashboard getDashboard) {
    this.getDashboard = getDashboard;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    UserContext context = (UserContext) auth.getPrincipal();
    var dashboard = this.getDashboard.execute(context.getId()).orElseThrow(); // TODO
    model.addAttribute("module", Module.DASHBOARD);
    model.addAttribute("context", new ContextModel(context));
    model.addAttribute("dashboard", dashboard);
    return "dashboard";
  }
}
