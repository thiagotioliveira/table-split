package dev.thiagooliveira.tablesplit.agent.listener;

import dev.thiagooliveira.tablesplit.agent.config.PrintAgentConfig;
import dev.thiagooliveira.tablesplit.agent.listener.model.TicketCreatedMessage;
import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TicketListener {

  private static final Logger log = LoggerFactory.getLogger(TicketListener.class);
  private final PrinterService printerService;
  private final PrintAgentConfig printAgentConfig;

  public TicketListener(
      PrinterService printerService,
      PrintAgentConfig printAgentConfig) {
    this.printerService = printerService;
    this.printAgentConfig = printAgentConfig;
  }

  /** Called by RabbitManagementService via MessageListenerAdapter */
  public void receiveOrder(TicketCreatedMessage order) {
    log.info("Order for Table {} received successfully. Sending to printer...", order.tableCod());
    printerService.printOrder(order, printAgentConfig.getPrinter());
  }
}
