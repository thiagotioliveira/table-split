package dev.thiagooliveira.tablesplit.infrastructure.web.manager.account;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
@ManagerModule(Module.ACCOUNT)
public class AccountController {
  @GetMapping
  public String index() {
    return "account";
  }
}
