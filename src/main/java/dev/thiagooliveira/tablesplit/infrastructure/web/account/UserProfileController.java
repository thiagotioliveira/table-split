package dev.thiagooliveira.tablesplit.infrastructure.web.account;

import dev.thiagooliveira.tablesplit.application.account.GetUser;
import dev.thiagooliveira.tablesplit.application.account.UpdateUser;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.UserContext;
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

  private final GetUser getUser;
  private final UpdateUser updateUser;

  public UserProfileController(GetUser getUser, UpdateUser updateUser) {
    this.getUser = getUser;
    this.updateUser = updateUser;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    var context = (UserContext) auth.getPrincipal();
    model.addAttribute("module", Module.USER_PROFILE);
    model.addAttribute("context", new ContextModel(context));
    model.addAttribute("user", new UserProfileModel(context));
    return "profile";
  }

  @PostMapping
  public String updateProfile(
      Authentication auth,
      @ModelAttribute UserProfileModel userProfileModel,
      RedirectAttributes redirectAttributes) {
    var context = (UserContext) auth.getPrincipal();
    var command = userProfileModel.toCommand();
    this.updateUser.execute(context.getId(), command);

    context.setFirstName(command.firstName());
    context.setLastName(command.lastName());
    context.setEmail(command.email());
    context.setLanguage(command.language());

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.user.profile.updated"));
    return "redirect:/profile";
  }
}
