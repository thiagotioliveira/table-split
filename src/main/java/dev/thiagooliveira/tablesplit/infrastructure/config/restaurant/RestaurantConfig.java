package dev.thiagooliveira.tablesplit.infrastructure.config.restaurant;

import dev.thiagooliveira.tablesplit.application.EventPublisher;
import dev.thiagooliveira.tablesplit.application.restaurant.CreateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantConfig {

  @Bean
  public GetRestaurant getRestaurant(RestaurantRepository restaurantRepository) {
    return new GetRestaurant(restaurantRepository);
  }

  @Bean
  public UpdateRestaurant updateRestaurant(
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    return new UpdateRestaurant(eventPublisher, restaurantRepository);
  }

  @Bean
  public CreateRestaurant createRestaurant(
      EventPublisher eventPublisher, RestaurantRepository restaurantRepository) {
    return new CreateRestaurant(eventPublisher, restaurantRepository);
  }
}
