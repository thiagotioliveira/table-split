package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.COUNTER)
@RequestMapping("/counter")
public class CounterController {

  @GetMapping
  public String index(Authentication auth, Model model) {
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new org.springframework.security.access.AccessDeniedException(
          "Access denied: User not authenticated");
    }
    model.addAttribute("restaurantId", context.getRestaurant().getId().toString());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    model.addAttribute("userLanguage", context.getUser().getLanguage());
    model.addAttribute("currency", context.getRestaurant().getCurrency());
    return "counter";
  }
}
