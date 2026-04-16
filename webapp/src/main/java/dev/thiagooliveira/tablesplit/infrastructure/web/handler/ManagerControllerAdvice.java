package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = {"dev.thiagooliveira.tablesplit.infrastructure.web.manager"})
public class ManagerControllerAdvice {

  private final GetTickets getTickets;

  public ManagerControllerAdvice(GetTickets getTickets) {
    this.getTickets = getTickets;
  }

  @ModelAttribute
  public void addManagerAttributes(Authentication auth, HttpServletRequest request, Model model) {

    if (auth != null && auth.isAuthenticated()) {
      var account = (AccountContext) auth.getPrincipal();
      long count = 0;
      if (account.getSidebarModules().contains(Module.ORDERS)) {
        count = getTickets.countPending(account.getRestaurant().getId());
      }
      model.addAttribute("context", new ContextModel(auth, count));
    } else throw new RuntimeException("never to be here");

    var module = (Module) request.getAttribute("currentModule");
    if (module != null) {
      model.addAttribute("module", module);
    }
  }
}
