package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login-staff")
public class StaffLoginController {

  @GetMapping
  public String login() {
    return "login-staff";
  }
}
