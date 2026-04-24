package dev.thiagooliveira.tablesplit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TableSplitApplication {

  public static void main(String[] args) {
    SpringApplication.run(TableSplitApplication.class, args);
  }
}
