package dev.thiagooliveira.tablesplit.agent.service;

import dev.thiagooliveira.tablesplit.agent.model.IntegrationOrderDTO;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrinterService {

  private static final Logger log = LoggerFactory.getLogger(PrinterService.class);
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  public java.util.List<String> getAvailablePrinters() {
    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
    return java.util.Arrays.stream(printServices)
        .map(javax.print.PrintService::getName)
        .collect(java.util.stream.Collectors.toList());
  }

  public void printOrder(IntegrationOrderDTO order) {
    log.info("Starting print job for ticket: {}", order.ticketId());

    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new OrderPrintable(order));

    try {
      // In a real scenario, we might want to check if a printer is available
      // For now, we use the default system printer
      job.print();
      log.info("Print job sent to default printer successfully.");
    } catch (PrinterException e) {
      log.error("Failed to print order {}: {}", order.ticketId(), e.getMessage(), e);
    }
  }

  private static class OrderPrintable implements Printable {
    private final IntegrationOrderDTO order;

    public OrderPrintable(IntegrationOrderDTO order) {
      this.order = order;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
        throws PrinterException {
      if (pageIndex > 0) {
        return NO_SUCH_PAGE;
      }

      Graphics2D g2d = (Graphics2D) graphics;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

      int y = 20;
      g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
      g2d.drawString("TABLE SPLIT - NOVO PEDIDO", 10, y);
      y += 20;

      g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
      g2d.drawString("Mesa: " + order.tableCod(), 10, y);
      y += 15;
      g2d.drawString("Cliente: " + order.customerName(), 10, y);
      y += 15;
      g2d.drawString("Data: " + order.createdAt().format(DATE_FORMATTER), 10, y);
      y += 15;
      g2d.drawString("------------------------------------------", 10, y);
      y += 15;

      g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
      g2d.drawString(String.format("%-3s %-25s %8s", "Qtd", "Item", "Preço"), 10, y);
      y += 15;
      g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

      for (IntegrationOrderDTO.Item item : order.items()) {
        String itemName = item.name();
        if (itemName.length() > 25) {
          itemName = itemName.substring(0, 22) + "...";
        }
        g2d.drawString(
            String.format("%-3d %-25s %8.2f", item.quantity(), itemName, item.totalPrice()), 10, y);
        y += 15;
        if (item.note() != null && !item.note().isBlank()) {
          g2d.setFont(new Font("Monospaced", Font.ITALIC, 8));
          g2d.drawString("  Obs: " + item.note(), 10, y);
          y += 12;
          g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        }
      }

      y += 5;
      g2d.drawString("------------------------------------------", 10, y);
      y += 15;
      g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
      g2d.drawString(String.format("%-29s %8.2f", "TOTAL", order.total()), 10, y);
      y += 20;

      return PAGE_EXISTS;
    }
  }
}
