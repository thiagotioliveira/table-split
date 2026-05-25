package dev.thiagooliveira.tablesplit.infrastructure.account.web;

import dev.thiagooliveira.tablesplit.application.account.UpdatePassword;
import dev.thiagooliveira.tablesplit.application.account.UpdateUser;
import dev.thiagooliveira.tablesplit.infrastructure.account.web.model.UserPasswordModel;
import dev.thiagooliveira.tablesplit.infrastructure.account.web.model.UserProfileModel;
import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ManagerController(Module.USER_PROFILE)
@RequestMapping("/profile")
public class UserProfileController {

  private final TransactionalContext transactionalContext;
  private final UpdateUser updateUser;
  private final UpdatePassword updatePassword;
  private final PasswordEncoder passwordEncoder;

  public UserProfileController(
      TransactionalContext transactionalContext,
      UpdateUser updateUser,
      UpdatePassword updatePassword,
      PasswordEncoder passwordEncoder) {
    this.transactionalContext = transactionalContext;
    this.updateUser = updateUser;
    this.updatePassword = updatePassword;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    model.addAttribute(
        "user", new UserProfileModel(((AccountContext) auth.getPrincipal()).getUser()));
    model.addAttribute("password", new UserPasswordModel());
    return "profile";
  }

  @PostMapping
  public String updateProfile(
      Authentication auth,
      @ModelAttribute UserProfileModel userProfileModel,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    var command = userProfileModel.toCommand();
    this.transactionalContext.execute(
        () -> this.updateUser.execute(context.getUser().getId(), command));

    context.getUser().setFirstName(command.firstName());
    context.getUser().setLastName(command.lastName());
    context.getUser().setEmail(command.email());
    context.getUser().setLanguage(command.language());

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.user.profile.updated"));
    return "redirect:/profile";
  }

  @PostMapping("/password")
  public String updatePassword(
      Authentication auth,
      @ModelAttribute UserPasswordModel userPasswordModel,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    if (!passwordEncoder.matches(
        userPasswordModel.getCurrentPassword(), context.getUser().getPassword())) {
      throw new InfrastructureException("error.invalid.password");
    }
    this.transactionalContext.execute(
        () ->
            this.updatePassword.execute(context.getUser().getId(), userPasswordModel.toCommand()));
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.user.password.updated"));
    return "redirect:/profile";
  }

  @ExceptionHandler({InfrastructureException.class, IllegalArgumentException.class})
  public String handleException(Exception ex, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("alert", AlertModel.error(ex.getMessage()));
    return "redirect:/profile";
  }
}
