package dev.thiagooliveira.tablesplit.infrastructure.web.handler;

import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ManagerModuleInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    if (handler instanceof HandlerMethod method) {
      ManagerModule annotation = method.getBeanType().getAnnotation(ManagerModule.class);
      if (annotation != null) {
        request.setAttribute("currentModule", annotation.value());
      }
    }

    return true;
  }
}
