package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleGenericException(NoResourceFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("errorType", ex.getClass().getSimpleName());
        return "404";
    }
}
