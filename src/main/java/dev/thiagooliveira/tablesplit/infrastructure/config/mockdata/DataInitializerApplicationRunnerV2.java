package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateUserCommand;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

// @Component
public class DataInitializerApplicationRunnerV2 implements ApplicationRunner {

  private final MockContext context;
  private final CreateAccount createAccount;
  private final TransactionalContext transactionalContext;
  private final GetRestaurant getRestaurant;

  public DataInitializerApplicationRunnerV2(
      MockContext context,
      CreateAccount createAccount,
      TransactionalContext transactionalContext,
      GetRestaurant getRestaurant) {
    this.context = context;
    this.createAccount = createAccount;
    this.transactionalContext = transactionalContext;
    this.getRestaurant = getRestaurant;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var slug = "donamaria.restaurant";
    var user =
        this.transactionalContext.execute(
            () ->
                this.createAccount.execute(
                    new CreateAccountCommand(
                        new CreateUserCommand(
                            "Thiago",
                            "Oliveira",
                            "thiago@thiagoti.com",
                            "+351 963 927 988",
                            "Test#123",
                            Language.PT),
                        new CreateRestaurantCommand(
                            "Dona Maria",
                            slug,
                            "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.",
                            "+351 963 927 944",
                            "contato@donamaria.com.br",
                            "https://donamaria.com.br",
                            "Rua das Flores, 123 - Centro",
                            "EUR",
                            10))));

    var restaurant = this.getRestaurant.execute(slug).orElseThrow();
    this.context.initContext(
        user.getAccountId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        restaurant.getId(),
        restaurant.getName(),
        restaurant.getCurrency(),
        restaurant.getCustomerLanguages());
  }
}
