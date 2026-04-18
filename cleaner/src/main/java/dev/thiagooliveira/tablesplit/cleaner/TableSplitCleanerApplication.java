package dev.thiagooliveira.tablesplit.cleaner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TableSplitCleanerApplication {

  public static void main(String[] args) {
    SpringApplication.run(TableSplitCleanerApplication.class, args);
  }

  @Bean
  @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
      name = "cleaner.runner.enabled",
      havingValue = "true",
      matchIfMissing = true)
  public CommandLineRunner run(OrderCleanerService orderCleanerService) {
    return args -> {
      orderCleanerService.cleanOldOrders();
    };
  }
}
