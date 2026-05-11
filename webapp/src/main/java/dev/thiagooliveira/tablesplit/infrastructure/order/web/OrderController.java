package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.ORDERS)
@RequestMapping("/orders")
public class OrderController {

  public OrderController() {}

  @GetMapping
  public String index(Authentication auth, Model model) {
    populateModel(auth, model);
    return "orders";
  }

  private void populateModel(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();

    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currency", context.getRestaurant().getCurrency());
    model.addAttribute("zoneId", Time.getZoneId().getId());
    model.addAttribute("userLanguage", context.getUser().getLanguage());
    model.addAttribute("serviceFee", context.getRestaurant().getServiceFee());

    model.addAttribute("STATUS_PENDING", "PENDING");
    model.addAttribute("STATUS_PREPARING", "PREPARING");
    model.addAttribute("STATUS_READY", "READY");
    model.addAttribute("STATUS_DELIVERED", "DELIVERED");
    model.addAttribute("STATUS_CANCELLED", "CANCELLED");
  }
}
