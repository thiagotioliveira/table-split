package dev.thiagooliveira.tablesplit.infrastructure.web.account;

import dev.thiagooliveira.tablesplit.application.account.UpdateUser;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.account.model.UserProfileModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

  private final UpdateUser updateUser;

  public UserProfileController(UpdateUser updateUser) {
    this.updateUser = updateUser;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = new ContextModel(auth);
    model.addAttribute("module", Module.USER_PROFILE);
    model.addAttribute("context", context);
    model.addAttribute(
        "user", new UserProfileModel(((AccountContext) auth.getPrincipal()).getUser()));
    return "profile";
  }

  @PostMapping
  public String updateProfile(
      Authentication auth,
      @ModelAttribute UserProfileModel userProfileModel,
      RedirectAttributes redirectAttributes) {
    var context = (AccountContext) auth.getPrincipal();
    var command = userProfileModel.toCommand();
    this.updateUser.execute(context.getUser().getId(), command);

    context.getUser().setFirstName(command.firstName());
    context.getUser().setLastName(command.lastName());
    context.getUser().setEmail(command.email());
    context.getUser().setLanguage(command.language());

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.user.profile.updated"));
    return "redirect:/profile";
  }
}
