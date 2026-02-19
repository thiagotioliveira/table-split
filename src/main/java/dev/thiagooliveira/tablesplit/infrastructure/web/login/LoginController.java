package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

  @GetMapping("/register")
  public String register() {
    return "register";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/forgot-password")
  public String forgotPassword() {
    return "forgot-password";
  }
}
