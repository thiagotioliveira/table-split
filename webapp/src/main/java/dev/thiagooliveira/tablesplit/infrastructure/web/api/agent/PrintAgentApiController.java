package dev.thiagooliveira.tablesplit.infrastructure.web.api.agent;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.ValidateAndUseToken;
import dev.thiagooliveira.tablesplit.domain.restaurant.PrintAgentToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/print-agent")
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "app.integration.rabbit.enabled",
    havingValue = "true")
public class PrintAgentApiController {

  private final ValidateAndUseToken validateAndUseToken;
  private final GetRestaurant getRestaurant;
  private final dev.thiagooliveira.tablesplit.infrastructure.persistence.account
          .AccountJpaRepository
      accountRepository;

  @Value("${spring.rabbitmq.addresses:localhost:5672}")
  private String rabbitAddresses;

  @Value("${app.integration.rabbit.public-addresses:}")
  private String publicRabbitAddresses;

  @Value("${spring.rabbitmq.username:guest}")
  private String rabbitUsername;

  @Value("${spring.rabbitmq.password:guest}")
  private String rabbitPassword;

  public PrintAgentApiController(
      ValidateAndUseToken validateAndUseToken,
      GetRestaurant getRestaurant,
      dev.thiagooliveira.tablesplit.infrastructure.persistence.account.AccountJpaRepository
          accountRepository) {
    this.validateAndUseToken = validateAndUseToken;
    this.getRestaurant = getRestaurant;
    this.accountRepository = accountRepository;
  }

  @PostMapping("/activate")
  public ResponseEntity<PrintAgentConfigDTO> activate(@RequestBody ActivationRequest request) {
    try {
      PrintAgentToken token = validateAndUseToken.execute(request.token());

      var account =
          accountRepository
              .findById(token.getRestaurantId())
              .orElseThrow(() -> new IllegalArgumentException("Account not found"));

      if (account.getPlan() != dev.thiagooliveira.tablesplit.domain.account.Plan.PROFESSIONAL
          && account.getPlan() != dev.thiagooliveira.tablesplit.domain.account.Plan.ENTERPRISE) {
        return ResponseEntity.status(401).build();
      }

      String effectiveRabbitAddress =
          (publicRabbitAddresses != null && !publicRabbitAddresses.isBlank())
              ? publicRabbitAddresses
              : rabbitAddresses;

      var restaurant =
          getRestaurant
              .execute(token.getRestaurantId())
              .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

      PrintAgentConfigDTO config =
          new PrintAgentConfigDTO(
              token.getRestaurantId(),
              restaurant.getName(),
              effectiveRabbitAddress,
              rabbitUsername,
              rabbitPassword,
              "restaurant." + token.getRestaurantId() + ".queue",
              "restaurant." + token.getRestaurantId() + ".orders");

      return ResponseEntity.ok(config);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(401).build();
    }
  }

  public record ActivationRequest(String token) {}
}
