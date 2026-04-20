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

  public void printOrder(IntegrationOrderDTO order, String printerName) {
    log.info("Starting print job for ticket: {} on printer: {}", order.ticketId(), printerName);

    PrinterJob job = PrinterJob.getPrinterJob();

    if (printerName != null && !printerName.isEmpty()) {
      PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
      for (PrintService service : printServices) {
        if (service.getName().equals(printerName)) {
          try {
            job.setPrintService(service);
            break;
          } catch (PrinterException e) {
            log.error("Could not set printer service {}: {}", printerName, e.getMessage());
          }
        }
      }
    }

    job.setPrintable(new OrderPrintable(order));

    try {
      job.print();
      log.info("Print job sent successfully.");
    } catch (PrinterException e) {
      log.error("Failed to print order {}: {}", order.ticketId(), e.getMessage(), e);
    }
  }

  public void printTest(String printerName) {
    java.util.UUID testId = java.util.UUID.randomUUID();
    IntegrationOrderDTO testOrder =
        new IntegrationOrderDTO(
            testId,
            "MESA TESTE",
            "Admin",
            java.util.List.of(
                new IntegrationOrderDTO.Item(
                    java.util.UUID.randomUUID(),
                    "Batata Frita",
                    1,
                    new java.math.BigDecimal("25.00"),
                    new java.math.BigDecimal("25.00"),
                    "Sem sal"),
                new IntegrationOrderDTO.Item(
                    java.util.UUID.randomUUID(),
                    "Cerveja Gelada",
                    2,
                    new java.math.BigDecimal("9.00"),
                    new java.math.BigDecimal("18.00"),
                    null)),
            new java.math.BigDecimal("43.00"),
            java.time.ZonedDateTime.now());
    printOrder(testOrder, printerName);
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
