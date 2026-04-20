package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RegisterModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final boolean registerEnabled;

  public RegisterController(
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      @Value("${app.register.enabled}") boolean registerEnabled) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.registerEnabled = registerEnabled;
  }

  @ModelAttribute("languages")
  public Language[] languages() {
    return Language.values();
  }

  @ModelAttribute("cuisineTypeCodes")
  public CuisineType[] cuisineTypeCodes() {
    return CuisineType.values();
  }

  @ModelAttribute("restaurantTags")
  public RestaurantTag[] restaurantTags() {
    return RestaurantTag.values();
  }

  @ModelAttribute("plans")
  public Plan[] plans() {
    return new Plan[] {Plan.STARTER, Plan.PROFESSIONAL};
  }

  @ModelAttribute("maxProfessionalTables")
  public int maxProfessionalTables() {
    return Plan.PROFESSIONAL.getLimits().tables();
  }

  @ModelAttribute("averagePriceCodes")
  public AveragePrice[] averagePriceCodes() {
    return AveragePrice.values();
  }

  @GetMapping
  public String register(Authentication auth, Model model) {
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      return "redirect:/dashboard";
    }

    if (!registerEnabled) {
      model.addAttribute("alert", AlertModel.error("error.register.enabled.false"));
    }

    model.addAttribute("form", new RegisterModel());
    return "register";
  }

  @PostMapping
  public String register(
      @Valid @ModelAttribute("form") RegisterModel form,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request) {
    if (!registerEnabled) {
      model.addAttribute("alert", AlertModel.error("error.register.enabled.false"));
      return "register";
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute("alert", AlertModel.error("error.register.required.missing"));
      return "register";
    }
    var user =
        this.transactionalContext.execute(
            () ->
                this.createAccount.execute(
                    form.toCommand(
                        passwordEncoder,
                        dev.thiagooliveira.tablesplit.infrastructure.utils.Time.getZoneId())));
    var token =
        new UsernamePasswordAuthenticationToken(user.getEmail(), form.getUser().getPassword());

    Authentication authentication = authenticationManager.authenticate(token);

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(authentication);

    HttpSession session = request.getSession(true);
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    return "redirect:/dashboard";
  }

  @ExceptionHandler(UserAlreadyRegisteredException.class)
  public String handleUserAlreadyRegisteredException(
      UserAlreadyRegisteredException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.register.user.already.registered"));
    return "redirect:/register";
  }

  @ExceptionHandler(SlugAlreadyExist.class)
  public String handleSlugAlreadyExist(SlugAlreadyExist ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.restaurant.slug.already.exist"));
    return "redirect:/register";
  }
}
