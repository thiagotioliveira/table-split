package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = {"dev.thiagooliveira.tablesplit.infrastructure.web.manager"})
public class ManagerControllerAdvice {

  @ModelAttribute
  public void addManagerAttributes(Authentication auth, HttpServletRequest request, Model model) {

    if (auth != null && auth.isAuthenticated()) {
      model.addAttribute("context", new ContextModel(auth));
    } else throw new RuntimeException("never to be here");

    var module = (Module) request.getAttribute("currentModule");
    if (module != null) {
      model.addAttribute("module", module);
    } else throw new RuntimeException("never to be here");
  }
}
