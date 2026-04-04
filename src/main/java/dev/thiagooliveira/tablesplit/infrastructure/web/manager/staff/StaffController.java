package dev.thiagooliveira.tablesplit.infrastructure.web.manager.staff;

import dev.thiagooliveira.tablesplit.application.account.*;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.staff.model.StaffModel;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
@ManagerModule(dev.thiagooliveira.tablesplit.infrastructure.web.Module.STAFF)
public class StaffController {

  private final GetStaff getStaff;
  private final CreateStaff createStaff;
  private final EditStaff editStaff;
  private final DeleteStaff deleteStaff;
  private final PasswordEncoder passwordEncoder;
  private final TransactionalContext transactionalContext;

  public StaffController(
      GetStaff getStaff,
      CreateStaff createStaff,
      EditStaff editStaff,
      DeleteStaff deleteStaff,
      PasswordEncoder passwordEncoder,
      TransactionalContext transactionalContext) {
    this.getStaff = getStaff;
    this.createStaff = createStaff;
    this.editStaff = editStaff;
    this.deleteStaff = deleteStaff;
    this.passwordEncoder = passwordEncoder;
    this.transactionalContext = transactionalContext;
  }

  @GetMapping
  public String list(@AuthenticationPrincipal AccountContext context, Model model) {
    var staffs = this.getStaff.list(context.getRestaurant().getId());
    model.addAttribute("staffs", staffs.stream().map(StaffModel::new).toList());
    model.addAttribute("availableModules", Module.staffAvailableModules());
    if (!model.containsAttribute("form")) {
      model.addAttribute("form", new StaffModel());
    }
    return "staff";
  }

  @GetMapping("/new")
  public String create(@AuthenticationPrincipal AccountContext context, Model model) {
    return list(context, model);
  }

  @PostMapping("/new")
  public String create(
      @AuthenticationPrincipal AccountContext context,
      @Valid @ModelAttribute("form") StaffModel form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      return list(context, model);
    }

    this.transactionalContext.execute(
        () ->
            this.createStaff.execute(
                form.toCreateCommand(
                    context.getRestaurant().getId(),
                    context.getRestaurant().getDefaultLanguage(),
                    passwordEncoder)));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("staff.create.success"));
    return "redirect:/staff";
  }

  @GetMapping("/{id}/edit")
  public String edit(
      @PathVariable UUID id, @AuthenticationPrincipal AccountContext context, Model model) {
    var staff =
        this.getStaff
            .execute(id)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
    model.addAttribute("form", new StaffModel(staff));
    return list(context, model);
  }

  @PostMapping("/{id}/edit")
  public String edit(
      @PathVariable UUID id,
      @Valid @ModelAttribute("form") StaffModel form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes,
      @AuthenticationPrincipal AccountContext context) {

    if (bindingResult.hasErrors()) {
      return list(context, model);
    }

    form.setId(id.toString());
    this.transactionalContext.execute(
        () -> this.editStaff.execute(form.toUpdateCommand(passwordEncoder)));

    redirectAttributes.addFlashAttribute("alert", AlertModel.success("staff.edit.success"));
    return "redirect:/staff";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    this.transactionalContext.execute(() -> this.deleteStaff.execute(id));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("staff.delete.success"));
    return "redirect:/staff";
  }
}
