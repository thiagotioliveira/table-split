package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantLocaleResolverTest {

  @Mock private GetRestaurant getRestaurant;

  @Mock private HttpServletRequest request;

  private RestaurantLocaleResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new RestaurantLocaleResolver(getRestaurant);
    resolver.setDefaultLocale(Locale.ENGLISH);
  }

  @Test
  void shouldReturnRequestedLocaleWhenSupported() {
    Restaurant restaurant = new Restaurant();
    restaurant.setCustomerLanguages(List.of(Language.PT, Language.EN));
    restaurant.setDefaultLanguage(Language.PT);

    Locale requestedLocale = Locale.forLanguageTag("en");
    Locale result = resolver.adjustLocale(requestedLocale, restaurant);

    assertEquals(requestedLocale, result);

    Locale ptLocale = Locale.forLanguageTag("pt");
    Locale resultPt = resolver.adjustLocale(ptLocale, restaurant);
    assertEquals(ptLocale, resultPt);
  }

  @Test
  void shouldFallbackToDefaultLanguageWhenRequestedLocaleNotSupported() {
    Restaurant restaurant = new Restaurant();
    restaurant.setCustomerLanguages(List.of(Language.PT));
    restaurant.setDefaultLanguage(Language.PT);

    Locale requestedLocale = Locale.forLanguageTag("ja");
    Locale result = resolver.adjustLocale(requestedLocale, restaurant);

    assertEquals(Locale.forLanguageTag(Language.PT.name()), result);
  }

  @Test
  void shouldFallbackToRawLanguageIfDefaultNotEnum() {
    Restaurant restaurant = new Restaurant();
    restaurant.setCustomerLanguages(List.of(Language.PT));
    restaurant.setDefaultLanguage(Language.PT);

    Locale requestedLocale = Locale.forLanguageTag("fr");
    Locale result = resolver.adjustLocale(requestedLocale, restaurant);

    assertEquals(Locale.forLanguageTag(Language.PT.name()), result);
  }
}
