package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.NotFoundException;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public String handleGenericException(NoResourceFoundException ex, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticated = !new AuthenticationTrustResolverImpl().isAnonymous(authentication);
    if (authenticated) {
      model.addAttribute("homeLink", "/dashboard");
    } else {
      model.addAttribute("homeLink", "/");
    }
    model.addAttribute("message", ex.getMessage());
    model.addAttribute("errorType", ex.getClass().getSimpleName());
    return "404";
  }

  @ExceptionHandler(NotFoundException.class)
  public String handleNotFoundException(NotFoundException ex, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticated = !new AuthenticationTrustResolverImpl().isAnonymous(authentication);
    if (authenticated) {
      model.addAttribute("homeLink", "/dashboard");
    } else {
      model.addAttribute("homeLink", "/");
    }
    model.addAttribute("message", ex.getMessage());
    model.addAttribute("errorType", ex.getClass().getSimpleName());
    return "404";
  }

  @ExceptionHandler(InfrastructureException.class)
  public String handleInfrastructureException(InfrastructureException ex, Model model) {
    model.addAttribute("message", ex.getMessage());
    model.addAttribute("errorType", ex.getClass().getSimpleName());
    return "500";
  }
}
