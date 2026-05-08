package dev.thiagooliveira.tablesplit.infrastructure.account.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.ACCOUNT)
@RequestMapping("/account")
public class AccountController {
  @GetMapping
  public String index() {
    return "account";
  }
}
