package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Period;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
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

  public DataInitializerApplicationRunner(
      MockContext context, RestaurantJpaRepository restaurantJpaRepository) {
    this.context = context;
    this.restaurantJpaRepository = restaurantJpaRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var restaurant = new RestaurantEntity();
    restaurant.setId(UUID.randomUUID());
    restaurant.setName("Dona Maria");
    restaurant.setDescription("Comida brasileira de qualidade!");
    restaurant.setPhone("+351 963 927 944");
    restaurant.setEmail("contato@cantinabella.com");
    restaurant.setAddress("Rua das Flores, 123 - Centro");
    restaurant.getTags().add(new Tag("\uD83D\uDCF6", "Wi-Fi Grátis"));
    restaurant.setDefaultLanguage("pt-BR");
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
    restaurant.setHashPrimaryColor("#f97316");
    restaurant.setHashAccentColor("#10b981");
    restaurant = this.restaurantJpaRepository.save(restaurant);
    context.setRestaurantId(restaurant.getId());
  }
}
