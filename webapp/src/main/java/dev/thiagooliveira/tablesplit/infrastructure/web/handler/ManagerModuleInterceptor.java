package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ManagerModuleInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (handler instanceof HandlerMethod method) {
      ManagerModule annotation = method.getBeanType().getAnnotation(ManagerModule.class);
      if (annotation != null) {
        request.setAttribute("currentModule", annotation.value());
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AccountContext context) {
          var module = annotation.value();
          boolean hasAccess =
              context.getSidebarModules().stream().anyMatch(m -> m == module)
                  || context.getFooterModules().stream().anyMatch(m -> m == module);

          if (!hasAccess) {
            throw new ForbiddenException("No access to module: " + module);
          }
        }
      }
    }

    return true;
  }
}
