package dev.thiagooliveira.tablesplit.infrastructure.listener.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.CreateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.event.AccountCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Period;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreatedEventListener {

  private final CreateRestaurant createRestaurant;

  public AccountCreatedEventListener(CreateRestaurant createRestaurant) {
    this.createRestaurant = createRestaurant;
  }

  @EventListener
  public void on(AccountCreatedEvent event) {
    var restaurant = event.getRestaurantData();
    this.createRestaurant.execute(
        event.getAccountId(),
        new CreateRestaurantCommand(
            restaurant.getName(),
            restaurant.getSlug(),
            restaurant.getDescription(),
            restaurant.getWebsite(),
            restaurant.getPhone(),
            restaurant.getEmail(),
            restaurant.getAddress(),
            List.of(),
            List.of(),
            restaurant.getDefaultLanguage(),
            List.of(restaurant.getDefaultLanguage()),
            restaurant.getCurrency(),
            restaurant.getServiceFee(),
            "20-50", // TODO
            List.of(
                new BusinessHours(
                    DayOfWeek.MONDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(
                    DayOfWeek.TUESDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(
                    DayOfWeek.WEDNESDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(
                    DayOfWeek.THURSDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(
                    DayOfWeek.FRIDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(
                    DayOfWeek.SATURDAY.name(), false, List.of(new Period("10:00", "23:00"))),
                new BusinessHours(DayOfWeek.SUNDAY.name().toLowerCase(), true, List.of())),
            "#EA580C", // TODO
            "#FFEDD5" // TODO
            ));
  }
}
