package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.account.PendingStaffPassword;
import dev.thiagooliveira.tablesplit.domain.account.PendingStaffPasswordRepository;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.StaffPasswordResetRequestedEvent;
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
public class StaffLoginController {

  private static final Logger logger = LoggerFactory.getLogger(StaffLoginController.class);

  @org.springframework.beans.factory.annotation.Value("${app.version}")
  private String appVersion;

  private final GetRestaurant getRestaurant;
  private final StaffRepository staffRepository;
  private final PendingStaffPasswordRepository pendingStaffPasswordRepository;
  private final ApplicationEventPublisher eventPublisher;

  public StaffLoginController(
      GetRestaurant getRestaurant,
      StaffRepository staffRepository,
      PendingStaffPasswordRepository pendingStaffPasswordRepository,
      ApplicationEventPublisher eventPublisher) {
    this.getRestaurant = getRestaurant;
    this.staffRepository = staffRepository;
    this.pendingStaffPasswordRepository = pendingStaffPasswordRepository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.web.bind.annotation.ModelAttribute("appVersion")
  public String appVersion() {
    return appVersion;
  }

  @GetMapping("/login-staff")
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
    return "login-staff";
  }

  @GetMapping("/forgot-staff-password")
  public String forgotStaffPasswordPage(
      @RequestParam(value = "slug", required = false) String slug, Model model) {
    if (slug != null && !slug.isBlank()) {
      model.addAttribute("slug", slug);
    }
    return "forgot-staff-password";
  }

  @PostMapping("/forgot-staff-password")
  public String forgotStaffPassword(
      @RequestParam("slug") String slug,
      @RequestParam("email") String email,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {

    String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
    String normalizedSlug = slug == null ? "" : slug.trim().toLowerCase();

    try {
      getRestaurant
          .execute(normalizedSlug)
          .ifPresent(
              restaurant -> {
                staffRepository
                    .findByEmail(normalizedEmail)
                    .filter(staff -> staff.getRestaurantId().equals(restaurant.getId()))
                    .ifPresent(
                        staff -> {
                          UUID token = UUID.randomUUID();
                          // Reusing PendingStaffPassword entity for reset too
                          PendingStaffPassword pending =
                              new PendingStaffPassword(
                                  token, normalizedEmail, Time.nowLocalDateTime().plusHours(24));
                          pendingStaffPasswordRepository.save(pending);

                          String baseUrl =
                              request
                                  .getRequestURL()
                                  .toString()
                                  .replace(request.getRequestURI(), request.getContextPath());

                          eventPublisher.publishEvent(
                              new StaffPasswordResetRequestedEvent(
                                  token,
                                  normalizedEmail,
                                  staff.getFirstName(),
                                  restaurant.getName(),
                                  normalizedSlug,
                                  baseUrl));
                        });
              });
    } catch (Exception e) {
      logger.error(
          "[StaffLoginController] Error processing forgot-password for staff email {} at slug {}: {}",
          normalizedEmail,
          normalizedSlug,
          e.getMessage(),
          e);
    }

    // Always show success to prevent email enumeration
    redirectAttributes.addFlashAttribute("successEmail", normalizedEmail);
    redirectAttributes.addFlashAttribute("successSlug", normalizedSlug);
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.forgot.staff.password.sent"));

    // Redirect with slug parameter so form keeps context
    redirectAttributes.addAttribute("slug", normalizedSlug);
    return "redirect:/forgot-staff-password?success";
  }
}
