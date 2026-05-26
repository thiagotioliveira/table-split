package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.domain.account.PendingUserPasswordReset;
import dev.thiagooliveira.tablesplit.domain.account.PendingUserPasswordResetRepository;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.UserPasswordResetRequestedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class LoginController {

  private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

  @org.springframework.beans.factory.annotation.Value("${app.version}")
  private String appVersion;

  private final UserRepository userRepository;
  private final PendingUserPasswordResetRepository pendingUserPasswordResetRepository;
  private final ApplicationEventPublisher eventPublisher;

  public LoginController(
      UserRepository userRepository,
      PendingUserPasswordResetRepository pendingUserPasswordResetRepository,
      ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.pendingUserPasswordResetRepository = pendingUserPasswordResetRepository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.web.bind.annotation.ModelAttribute("appVersion")
  public String appVersion() {
    return appVersion;
  }

  @GetMapping("/login")
  public String login(Authentication auth, HttpServletRequest request, Model model) {
    request.getSession(true);

    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      return "redirect:/dashboard";
    }
    Exception exception =
        (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    if (exception != null) {

      if (exception instanceof BadCredentialsException
          || exception instanceof UsernameNotFoundException) {
        model.addAttribute("alert", AlertModel.error("error.login.bad.credentials"));
      } else if (exception instanceof LockedException) {
        model.addAttribute("alert", AlertModel.error("error.login.account.locked"));
      } else if (exception instanceof DisabledException) {
        model.addAttribute("alert", AlertModel.error("error.login.account.disabled"));
      } else {
        model.addAttribute("alert", AlertModel.error("error.login.generic"));
      }
    }
    return "login";
  }

  @GetMapping("/forgot-password")
  public String forgotPasswordPage(HttpServletRequest request, Model model) {
    request.getSession(true);
    return "forgot-password";
  }

  @PostMapping("/forgot-password")
  public String forgotPassword(
      @RequestParam("email") String email,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

    try {
      userRepository
          .findByEmail(normalizedEmail)
          .ifPresent(
              user -> {
                UUID token = UUID.randomUUID();
                PendingUserPasswordReset pending =
                    new PendingUserPasswordReset(
                        token, normalizedEmail, Time.nowLocalDateTime().plusHours(24));
                pendingUserPasswordResetRepository.save(pending);

                String baseUrl =
                    request
                        .getRequestURL()
                        .toString()
                        .replace(request.getRequestURI(), request.getContextPath());

                eventPublisher.publishEvent(
                    new UserPasswordResetRequestedEvent(
                        token, normalizedEmail, user.getFirstName(), baseUrl));
              });
    } catch (Exception e) {
      logger.error(
          "[LoginController] Error processing forgot-password for {}: {}",
          normalizedEmail,
          e.getMessage(),
          e);
    }

    // Always show success to avoid email enumeration
    redirectAttributes.addFlashAttribute("successEmail", normalizedEmail);
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.forgot.password.sent"));
    return "redirect:/forgot-password?success";
  }

  @GetMapping("/reset-password")
  public String resetPasswordPage(
      @RequestParam("token") UUID token, Model model, RedirectAttributes redirectAttributes) {

    var pendingOpt = pendingUserPasswordResetRepository.findById(token);
    if (pendingOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.error("error.staff.activation.token.invalid"));
      return "redirect:/forgot-password";
    }

    var pending = pendingOpt.get();
    if (pending.isExpired()) {
      pendingUserPasswordResetRepository.deleteById(token);
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.error("error.staff.activation.token.expired"));
      return "redirect:/forgot-password";
    }

    model.addAttribute("token", token);
    model.addAttribute("userEmail", pending.getEmail());
    return "reset-password";
  }

  @PostMapping("/reset-password")
  public String resetPassword(
      @RequestParam("token") UUID token,
      @RequestParam("password") String password,
      Model model,
      RedirectAttributes redirectAttributes) {

    try {
      var pendingOpt = pendingUserPasswordResetRepository.findById(token);
      if (pendingOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.invalid"));
        return "redirect:/forgot-password";
      }

      var pending = pendingOpt.get();
      if (pending.isExpired()) {
        pendingUserPasswordResetRepository.deleteById(token);
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.expired"));
        return "redirect:/forgot-password";
      }

      dev.thiagooliveira.tablesplit.domain.account.PasswordValidator.validate(password);

      userRepository
          .findByEmail(pending.getEmail())
          .ifPresent(
              user -> {
                user.setPassword(
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode(password));
                userRepository.save(user);
                pendingUserPasswordResetRepository.deleteById(token);
              });

      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.user.password.updated"));
      return "redirect:/login?passwordReset";

    } catch (IllegalArgumentException ex) {
      model.addAttribute("token", token);
      model.addAttribute("alert", AlertModel.error(ex.getMessage()));
      return "reset-password";
    } catch (Exception e) {
      logger.error(
          "[LoginController] Error on reset-password (token={}): {}", token, e.getMessage(), e);
      model.addAttribute("token", token);
      model.addAttribute("alert", AlertModel.error("error.login.generic"));
      return "reset-password";
    }
  }
}
