package dev.thiagooliveira.tablesplit.infrastructure.web.security.advice;

import dev.thiagooliveira.tablesplit.application.account.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.ManagerController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = ManagerController.class)
public class ManagerControllerAdvice {

  @org.springframework.beans.factory.annotation.Value("${app.version}")
  private String appVersion;

  public ManagerControllerAdvice() {}

  @ModelAttribute
  public void addManagerAttributes(Authentication auth, HttpServletRequest request, Model model) {

    if (auth != null && auth.isAuthenticated()) {
      model.addAttribute("context", new ManagerContextModel(auth));
    } else throw new RuntimeException("never to be here");

    var module = (Module) request.getAttribute("currentModule");
    if (module != null) {
      model.addAttribute("module", module);
    }
    model.addAttribute("appVersion", appVersion);
  }

  @ExceptionHandler(PlanLimitExceededException.class)
  public String handlePlanLimitExceededException(
      PlanLimitExceededException ex,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("alert", AlertModel.error(ex.getMessage()));
    String referer = request.getHeader("Referer");
    if (referer != null && !referer.isEmpty()) {
      return "redirect:" + referer;
    }
    return "redirect:/dashboard";
  }
}
