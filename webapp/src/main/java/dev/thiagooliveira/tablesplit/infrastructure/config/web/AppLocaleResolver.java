package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

public class AppLocaleResolver extends SessionLocaleResolver {

  private final GetRestaurant getRestaurant;
  private static final Pattern SLUG_PATTERN = Pattern.compile("/@([a-zA-Z0-9.-]+)");

  public AppLocaleResolver(GetRestaurant getRestaurant) {
    this.getRestaurant = getRestaurant;
  }

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    Locale requestedLocale = super.resolveLocale(request);
    String slug = extractSlug(request);

    if (slug == null) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      boolean authenticated =
          authentication != null
              && !new AuthenticationTrustResolverImpl().isAnonymous(authentication);
      if (authenticated) {
        var context = (AccountContext) authentication.getPrincipal();
        return Locale.forLanguageTag(context.getUser().getLanguage().name());
      } else {
        return requestedLocale;
      }
    }

    return getRestaurant
        .execute(slug)
        .map(restaurant -> adjustLocale(requestedLocale, restaurant))
        .orElse(requestedLocale);
  }

  private String extractSlug(HttpServletRequest request) {
    String uri = request.getRequestURI();
    Matcher matcher = SLUG_PATTERN.matcher(uri);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  Locale adjustLocale(Locale requestedLocale, Restaurant restaurant) {
    boolean isSupported =
        restaurant.getCustomerLanguages().stream()
            .anyMatch(lang -> lang.name().equalsIgnoreCase(requestedLocale.getLanguage()));

    if (isSupported) {
      return requestedLocale;
    }

    // Fallback to default language
    return Locale.forLanguageTag(restaurant.getCustomerLanguages().getFirst().name());
  }

  @Override
  public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    super.setLocale(request, response, locale);
  }
}
