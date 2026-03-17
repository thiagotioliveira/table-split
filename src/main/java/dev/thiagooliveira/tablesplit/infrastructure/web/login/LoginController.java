package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

  @GetMapping("/login")
  public String login(HttpServletRequest request, Model model) {
    Exception exception =
        (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    if (exception != null) {

      if (exception instanceof BadCredentialsException) {
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
  public String forgotPassword() {
    return "forgot-password";
  }
}
