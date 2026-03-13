package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard;

import dev.thiagooliveira.tablesplit.application.dashboard.GetDashboard;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.model.DashboardModel;
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
    var context = new ContextModel(auth);
    var dashboard = this.getDashboard.execute(context.getUser().getId()).orElseThrow(); // TODO
    model.addAttribute("module", Module.DASHBOARD);
    model.addAttribute("context", context);
    model.addAttribute("dashboard", new DashboardModel(dashboard.getAttributes()));
    return "dashboard";
  }
}
