package dev.thiagooliveira.tablesplit.infrastructure.config.local;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateUserCommand;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializerApplicationRunner implements ApplicationRunner {

  private final Time time;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final PasswordEncoder passwordEncoder;

  public DemoDataInitializerApplicationRunner(
      Time time,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      PasswordEncoder passwordEncoder) {
    this.time = time;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    this.transactionalContext.execute(
        () ->
            this.createAccount.execute(
                new CreateAccountCommand(
                    new CreateUserCommand(
                        "Thiago",
                        "Oliveira",
                        "thiago@thiagoti.com",
                        "+351 963 927 988",
                        passwordEncoder.encode("Test#123"),
                        Language.PT),
                    new CreateRestaurantCommand(
                        "Restaurante Dona Maria",
                        "donamariarestaurant",
                        "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.",
                        "+351 963 927 944",
                        "contato@donamaria.com.br",
                        "https://donamaria.com.br",
                        "Rua das Flores, 123 - Centro",
                        Currency.EUR,
                        10),
                    time.getZoneId())));
  }
}
