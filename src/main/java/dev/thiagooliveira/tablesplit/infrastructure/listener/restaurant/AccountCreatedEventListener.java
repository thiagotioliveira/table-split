package dev.thiagooliveira.tablesplit.infrastructure.listener.restaurant;

import dev.thiagooliveira.tablesplit.application.restaurant.CreateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.common.Language;
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
    var restaurantDetails = event.getDetails().getRestaurantDetails();
    this.createRestaurant.execute(
        event.getAccountId(),
        new CreateRestaurantCommand(
            restaurantDetails.getName(),
            restaurantDetails.getSlug(),
            restaurantDetails.getDescription(),
            restaurantDetails.getWebsite(),
            restaurantDetails.getPhone(),
            restaurantDetails.getEmail(),
            restaurantDetails.getAddress(),
            null,
            List.of(),
            List.of(Language.PT, Language.EN),
            restaurantDetails.getCurrency(),
            restaurantDetails.getServiceFee(),
            restaurantDetails.getNumberOfTables(),
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
                    DayOfWeek.SATURDAY.name().toLowerCase(),
                    false,
                    List.of(new Period("10:00", "23:00"))),
                new BusinessHours(DayOfWeek.SUNDAY.name().toLowerCase(), true, List.of())),
            "#c9a050",
            "#e6efe9",
            Language.PT));
  }
}
