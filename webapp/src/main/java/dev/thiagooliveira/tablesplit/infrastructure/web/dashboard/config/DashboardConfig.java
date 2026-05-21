package dev.thiagooliveira.tablesplit.infrastructure.web.dashboard.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DashboardConfig {

  private final DashboardProperties properties;

  public DashboardConfig(DashboardProperties properties) {
    this.properties = properties;
  }

  @Bean(name = "dashboardExecutor")
  public Executor dashboardExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(properties.getCorePoolSize());
    executor.setMaxPoolSize(properties.getMaxPoolSize());
    executor.setQueueCapacity(properties.getQueueCapacity());
    executor.setThreadNamePrefix(properties.getThreadNamePrefix());
    executor.setRejectedExecutionHandler(
        new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
