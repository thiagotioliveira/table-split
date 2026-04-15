package dev.thiagooliveira.tablesplit.infrastructure.web.manager.reports;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
@ManagerModule(Module.REPORTS)
public class ReportController {

  @GetMapping
  public String index() {
    return "reports";
  }
}
