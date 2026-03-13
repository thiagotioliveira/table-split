package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RegisterModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
  private final Time time;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;

  public RegisterController(
      Time time,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      TransactionalContext transactionalContext,
      CreateAccount createAccount) {
    this.time = time;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
  }

  @GetMapping
  public String register(Model model) {
    model.addAttribute("form", new RegisterModel());
    return "register";
  }

  @PostMapping
  public String register(
      @ModelAttribute RegisterModel registerModel,
      RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
    var user =
        this.transactionalContext.execute(
            () ->
                this.createAccount.execute(
                    registerModel.toCommand(passwordEncoder, time.getZoneId())));
    var token =
        new UsernamePasswordAuthenticationToken(
            user.getEmail(), registerModel.getUser().getPassword());

    Authentication authentication = authenticationManager.authenticate(token);

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(authentication);

    HttpSession session = request.getSession(true);
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    return "redirect:/dashboard";
  }
}
