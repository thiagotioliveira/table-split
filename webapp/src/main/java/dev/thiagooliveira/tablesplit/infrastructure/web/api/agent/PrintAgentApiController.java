package dev.thiagooliveira.tablesplit.infrastructure.web.api.agent;

import dev.thiagooliveira.tablesplit.application.printing.PrintAgentService;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.PrintAgentTokenEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/print-agent")
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "app.integration.rabbit.enabled",
    havingValue = "true")
public class PrintAgentApiController {

  private final PrintAgentService printAgentService;

  @Value("${spring.rabbitmq.addresses:localhost:5672}")
  private String rabbitAddresses;

  @Value("${app.integration.rabbit.public-addresses:}")
  private String publicRabbitAddresses;

  @Value("${spring.rabbitmq.username:guest}")
  private String rabbitUsername;

  @Value("${spring.rabbitmq.password:guest}")
  private String rabbitPassword;

  public PrintAgentApiController(PrintAgentService printAgentService) {
    this.printAgentService = printAgentService;
  }

  @PostMapping("/activate")
  public ResponseEntity<PrintAgentConfigDTO> activate(@RequestBody ActivationRequest request) {
    try {
      PrintAgentTokenEntity token = printAgentService.validateAndUseToken(request.token());

      String effectiveRabbitAddress =
          (publicRabbitAddresses != null && !publicRabbitAddresses.isBlank())
              ? publicRabbitAddresses
              : rabbitAddresses;

      PrintAgentConfigDTO config =
          new PrintAgentConfigDTO(
              token.getRestaurant().getId(),
              token.getRestaurant().getName(),
              effectiveRabbitAddress,
              rabbitUsername,
              rabbitPassword,
              "restaurant." + token.getRestaurant().getId() + ".queue",
              "restaurant." + token.getRestaurant().getId() + ".orders");

      return ResponseEntity.ok(config);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(401).build();
    }
  }

  public record ActivationRequest(String token) {}
}
