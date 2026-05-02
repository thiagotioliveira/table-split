package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.notification.ListActiveWaiterCalls;
import dev.thiagooliveira.tablesplit.application.order.GetTickets;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = {"dev.thiagooliveira.tablesplit.infrastructure.web.manager"})
public class ManagerControllerAdvice {

  private final GetTickets getTickets;
  private final ListActiveWaiterCalls listActiveWaiterCalls;
  private final dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount
      getFeedbackUnreadCount;

  @org.springframework.beans.factory.annotation.Value("${app.version}")
  private String appVersion;

  public ManagerControllerAdvice(
      GetTickets getTickets,
      ListActiveWaiterCalls listActiveWaiterCalls,
      dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount
          getFeedbackUnreadCount) {
    this.getTickets = getTickets;
    this.listActiveWaiterCalls = listActiveWaiterCalls;
    this.getFeedbackUnreadCount = getFeedbackUnreadCount;
  }

  @ModelAttribute
  public void addManagerAttributes(Authentication auth, HttpServletRequest request, Model model) {

    if (auth != null && auth.isAuthenticated()) {
      var account = (AccountContext) auth.getPrincipal();
      long ordersCount = 0;
      if (account.getSidebarModules().contains(Module.ORDERS)) {
        ordersCount = getTickets.countPending(account.getRestaurant().getId());
      }
      long waiterCount = 0;
      if (account.getSidebarModules().contains(Module.TABLES)) {
        waiterCount = listActiveWaiterCalls.execute(account.getRestaurant().getId()).size();
      }
      long feedbackCount = 0;
      if (account.getSidebarModules().contains(Module.FEEDBACKS)) {
        feedbackCount = getFeedbackUnreadCount.execute(account.getRestaurant().getId());
      }
      model.addAttribute(
          "context", new ContextModel(auth, ordersCount, waiterCount, feedbackCount));
    } else throw new RuntimeException("never to be here");

    var module = (Module) request.getAttribute("currentModule");
    if (module != null) {
      model.addAttribute("module", module);
    }
    model.addAttribute("appVersion", appVersion);
  }

  @ExceptionHandler(PlanLimitExceededException.class)
  public String handlePlanLimitExceededException(
      PlanLimitExceededException ex,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("alert", AlertModel.error(ex.getMessage()));
    String referer = request.getHeader("Referer");
    if (referer != null && !referer.isEmpty()) {
      return "redirect:" + referer;
    }
    return "redirect:/dashboard";
  }
}
