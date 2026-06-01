package dev.thiagooliveira.tablesplit.infrastructure.order.web;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ManagerController(Module.FEEDBACKS)
@RequestMapping("/feedbacks")
public class FeedbackController {

  private final dev.thiagooliveira.tablesplit.application.order.MarkFeedbacksAsRead
      markFeedbacksAsRead;

  public FeedbackController(
      dev.thiagooliveira.tablesplit.application.order.MarkFeedbacksAsRead markFeedbacksAsRead) {
    this.markFeedbacksAsRead = markFeedbacksAsRead;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    if (auth == null || !(auth.getPrincipal() instanceof AccountContext context)) {
      throw new org.springframework.security.access.AccessDeniedException(
          "Access denied: User not authenticated");
    }
    markFeedbacksAsRead.execute(context.getRestaurant().getId());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    return "feedbacks";
  }
}
