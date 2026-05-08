package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.RESERVATION)
@RequestMapping("/reservations")
public class ReservationController {

  @GetMapping
  public String index(Model model) {
    return "reservation";
  }
}
