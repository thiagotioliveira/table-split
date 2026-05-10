package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.COUNTER)
@RequestMapping("/counter")
public class CounterController {

  @GetMapping
  public String index() {
    return "counter";
  }
}
