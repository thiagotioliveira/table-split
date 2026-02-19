package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
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
    restaurant = this.restaurantJpaRepository.save(restaurant);
    context.setRestaurantId(restaurant.getId());
  }
}
