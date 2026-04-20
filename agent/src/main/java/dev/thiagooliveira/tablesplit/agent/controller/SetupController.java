package dev.thiagooliveira.tablesplit.agent.controller;

import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SetupController {

  private final PrinterService printerService;

  public SetupController(PrinterService printerService) {
    this.printerService = printerService;
  }

  @GetMapping("/")
  public String setup(Model model) {
    List<String> printers = printerService.getAvailablePrinters();
    model.addAttribute("printers", printers);
    model.addAttribute("connected", false); // Default for now
    model.addAttribute("configToken", "");
    model.addAttribute("selectedPrinter", null);
    model.addAttribute("queueName", "Aguardando conexão...");
    model.addAttribute("rabbitHost", "localhost");
    return "setup";
  }

  @PostMapping("/configure")
  public String configure(@RequestParam String token, @RequestParam String printer) {
    // Logic to validate token and save configuration will be implemented next
    return "redirect:/?success=true";
  }
}
