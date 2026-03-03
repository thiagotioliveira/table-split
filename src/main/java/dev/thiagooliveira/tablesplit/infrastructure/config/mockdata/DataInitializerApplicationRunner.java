package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializerApplicationRunner implements ApplicationRunner {

  private final MockContext context;
  private final RestaurantJpaRepository restaurantJpaRepository;
  private final CategoryJpaRepository categoryJpaRepository;

  public DataInitializerApplicationRunner(
      MockContext context,
      RestaurantJpaRepository restaurantJpaRepository,
      CategoryJpaRepository categoryJpaRepository) {
    this.context = context;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.categoryJpaRepository = categoryJpaRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var customerLanguages = List.of(Language.PT, Language.EN);
    var restaurant = new RestaurantEntity();
    restaurant.setId(UUID.randomUUID());
    restaurant.setName("Restaurante Dona Maria");
    restaurant.setSlug("donamaria.restaurant");
    restaurant.setDescription(
        "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.");
    restaurant.setWebsite("https://donamaria.com.br");
    restaurant.setPhone("+351 963 927 944");
    restaurant.setEmail("contato@donamaria.com.br");
    restaurant.setAddress("Rua das Flores, 123 - Centro");
    restaurant.getCuisineType().add(CuisineType.BRAZILIAN);
    restaurant.getTags().add(Tag.WIFI);
    restaurant.getTags().add(Tag.DELIVERY);
    restaurant.getTags().add(Tag.RESERVATIONS);
    restaurant.getTags().add(Tag.GROUPS);
    restaurant.getTags().add(Tag.CARDS);
    restaurant.setDefaultLanguage("pt-BR");
    restaurant.setCustomerLanguages(customerLanguages);
    restaurant.setCurrency("EUR");
    restaurant.setServiceFee(10);
    restaurant.setAveragePrice("20-50");
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.MONDAY.name().toLowerCase(),
                true,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.TUESDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.WEDNESDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.THURSDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.FRIDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "02:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.SATURDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "02:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.SUNDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant.setHashPrimaryColor("#EA580C");
    restaurant.setHashAccentColor("#FFEDD5");
    //    restaurant.setHashPrimaryColor("#15803D");
    //    restaurant.setHashAccentColor("#FEF9C3");
    restaurant = this.restaurantJpaRepository.save(restaurant);

    context.initContext(
        restaurant.getId(), restaurant.getName(), restaurant.getCurrency(), customerLanguages);

    var category = new CategoryEntity();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurant.getId());
    category.setNumOrder(1);
    category.getName().put(Language.PT, "Entradas");
    category.getName().put(Language.EN, "Starters");

    category = this.categoryJpaRepository.save(category);
  }
}
