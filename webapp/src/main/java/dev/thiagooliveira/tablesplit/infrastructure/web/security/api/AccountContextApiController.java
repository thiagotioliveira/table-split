package dev.thiagooliveira.tablesplit.infrastructure.web.security.api;

import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.api.spec.v1.ContextApi;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.api.spec.v1.model.AccountContextResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.api.spec.v1.model.ModuleResponse;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.context.AccountContext;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/v1/account")
public class AccountContextApiController implements ContextApi {

  private final MessageSource messageSource;

  public AccountContextApiController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public ResponseEntity<AccountContextResponse> getAccountContext() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AccountContext context = (AccountContext) auth.getPrincipal();
    Locale locale = Locale.forLanguageTag(context.getUser().getLanguage().name());

    List<ModuleResponse> sidebar = mapModules(context.getSidebarModules(), locale);
    List<ModuleResponse> footer = mapModules(context.getFooterModules(), locale);

    AccountContextResponse response = new AccountContextResponse();
    response.setRestaurantName(context.getRestaurant().getName());
    response.setUserName(context.getUser().getName());
    response.setUserEmail(context.getUser().getEmail());
    response.setUserAvatar(context.getUser().getAvatar());
    response.setSidebarModules(sidebar);
    response.setFooterModules(footer);

    return ResponseEntity.ok(response);
  }

  private List<ModuleResponse> mapModules(List<Module> modules, Locale locale) {
    return modules.stream()
        .map(
            m -> {
              ModuleResponse res = new ModuleResponse();
              res.setName(m.name());
              res.setLabel(messageSource.getMessage(m.getName(), null, locale));
              res.setIcon(m.getIcon());
              res.setHref(m.getHref());
              res.setActive(m.isActive());
              return res;
            })
        .toList();
  }

  private Locale getLocale() {
    ServletRequestAttributes attr =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attr.getRequest().getLocale();
  }
}
