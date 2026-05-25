package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.domain.account.PasswordValidator;
import dev.thiagooliveira.tablesplit.domain.account.PendingStaffPasswordRepository;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login-staff/set-password")
public class StaffPasswordController {

  private static final Logger logger = LoggerFactory.getLogger(StaffPasswordController.class);

  private final PendingStaffPasswordRepository pendingStaffPasswordRepository;
  private final StaffRepository staffRepository;
  private final PasswordEncoder passwordEncoder;
  private final TransactionalContext transactionalContext;

  public StaffPasswordController(
      PendingStaffPasswordRepository pendingStaffPasswordRepository,
      StaffRepository staffRepository,
      PasswordEncoder passwordEncoder,
      TransactionalContext transactionalContext) {
    this.pendingStaffPasswordRepository = pendingStaffPasswordRepository;
    this.staffRepository = staffRepository;
    this.passwordEncoder = passwordEncoder;
    this.transactionalContext = transactionalContext;
  }

  @GetMapping
  public String setPasswordPage(
      @RequestParam("token") UUID token,
      @RequestParam("slug") String slug,
      Model model,
      RedirectAttributes redirectAttributes) {

    try {
      var pendingOpt = this.pendingStaffPasswordRepository.findById(token);
      if (pendingOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.invalid"));
        return "redirect:/login-staff?slug=" + slug;
      }

      var pending = pendingOpt.get();
      if (pending.isExpired()) {
        this.transactionalContext.execute(
            () -> this.pendingStaffPasswordRepository.deleteById(token));
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.expired"));
        return "redirect:/login-staff?slug=" + slug;
      }

      model.addAttribute("token", token);
      model.addAttribute("slug", slug);
      return "set-password";
    } catch (Exception e) {
      logger.error(
          "[StaffPasswordController] Error on GET set-password (token={}, slug={}): {}",
          token,
          slug,
          e.getMessage(),
          e);
      redirectAttributes.addFlashAttribute("alert", AlertModel.error("error.login.generic"));
      return "redirect:/login-staff?slug=" + slug;
    }
  }

  @PostMapping
  public String setPassword(
      @RequestParam("token") UUID token,
      @RequestParam("slug") String slug,
      @RequestParam("password") String password,
      Model model,
      RedirectAttributes redirectAttributes) {

    try {
      var pendingOpt = this.pendingStaffPasswordRepository.findById(token);
      if (pendingOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.invalid"));
        return "redirect:/login-staff?slug=" + slug;
      }

      var pending = pendingOpt.get();
      if (pending.isExpired()) {
        this.transactionalContext.execute(
            () -> this.pendingStaffPasswordRepository.deleteById(token));
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("error.staff.activation.token.expired"));
        return "redirect:/login-staff?slug=" + slug;
      }

      // Domain raw password validation
      PasswordValidator.validate(password);

      this.transactionalContext.execute(
          () -> {
            var staff =
                this.staffRepository
                    .findByEmail(pending.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Staff user not found"));

            staff.setPassword(passwordEncoder.encode(password));
            this.staffRepository.save(staff);

            this.pendingStaffPasswordRepository.deleteById(token);
            return null;
          });

      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("alert.staff.activation.success"));
      return "redirect:/login-staff?slug=" + slug;

    } catch (IllegalArgumentException ex) {
      model.addAttribute("token", token);
      model.addAttribute("slug", slug);
      model.addAttribute("alert", AlertModel.error(ex.getMessage()));
      return "set-password";
    } catch (Exception e) {
      logger.error(
          "[StaffPasswordController] Error on POST set-password (token={}, slug={}): {}",
          token,
          slug,
          e.getMessage(),
          e);
      model.addAttribute("token", token);
      model.addAttribute("slug", slug);
      model.addAttribute("alert", AlertModel.error("error.login.generic"));
      return "set-password";
    }
  }
}
