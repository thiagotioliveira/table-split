package dev.thiagooliveira.tablesplit.infrastructure.web.security;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Controller;

@Controller
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagerController {
  Module value();
}
