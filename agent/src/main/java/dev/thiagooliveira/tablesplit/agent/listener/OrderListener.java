package dev.thiagooliveira.tablesplit.agent.listener;

import dev.thiagooliveira.tablesplit.agent.model.IntegrationOrderDTO;
import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

  private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
  private final PrinterService printerService;
  private final dev.thiagooliveira.tablesplit.agent.config.AgentConfig agentConfig;

  public OrderListener(
      PrinterService printerService,
      dev.thiagooliveira.tablesplit.agent.config.AgentConfig agentConfig) {
    this.printerService = printerService;
    this.agentConfig = agentConfig;
  }

  /** Called by RabbitManagementService via MessageListenerAdapter */
  public void receiveOrder(IntegrationOrderDTO order) {
    log.info("Order for Table {} received successfully. Sending to printer...", order.tableCod());
    printerService.printOrder(order, agentConfig.getPrinter());
  }
}
