package dev.thiagooliveira.tablesplit.infrastructure.web.manager.staff;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
@ManagerModule(Module.STAFF)
public class StaffController {

  @GetMapping
  public String index() {
    return "staff";
  }
}
