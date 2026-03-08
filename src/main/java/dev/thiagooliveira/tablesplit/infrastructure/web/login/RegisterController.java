package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.config.mockdata.MockContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RegisterModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {
  private final MockContext context;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final GetRestaurant getRestaurant;

  public RegisterController(
      MockContext context,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      GetRestaurant getRestaurant) {
    this.context = context;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.getRestaurant = getRestaurant;
  }

  @GetMapping
  public String register(Model model) {
    model.addAttribute("form", new RegisterModel());
    return "register";
  }

  @PostMapping
  public String register(
      @ModelAttribute RegisterModel registerModel, RedirectAttributes redirectAttributes) {
    this.transactionalContext.execute(() -> this.createAccount.execute(registerModel.toCommand()));
    var restaurant =
        this.getRestaurant.execute(registerModel.getRestaurant().getSlug()).orElseThrow();
    this.context.initContext(
        restaurant.getId(),
        restaurant.getName(),
        restaurant.getCurrency(),
        restaurant.getCustomerLanguages());
    return "redirect:/menu";
  }
}
