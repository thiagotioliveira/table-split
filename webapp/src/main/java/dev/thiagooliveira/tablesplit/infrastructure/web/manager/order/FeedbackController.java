package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order;

import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/feedbacks")
@ManagerModule(Module.FEEDBACKS)
public class FeedbackController {

  private final dev.thiagooliveira.tablesplit.application.order.MarkFeedbacksAsRead
      markFeedbacksAsRead;

  public FeedbackController(
      dev.thiagooliveira.tablesplit.application.order.MarkFeedbacksAsRead markFeedbacksAsRead) {
    this.markFeedbacksAsRead = markFeedbacksAsRead;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    AccountContext context = (AccountContext) auth.getPrincipal();
    markFeedbacksAsRead.execute(context.getRestaurant().getId());
    model.addAttribute("currencySymbol", context.getRestaurant().getCurrency().getSymbol());
    return "feedbacks";
  }
}
