package dev.thiagooliveira.tablesplit.agent.controller;

import dev.thiagooliveira.tablesplit.agent.config.PrintAgentConfig;
import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import dev.thiagooliveira.tablesplit.agent.service.RabbitManagementService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SetupController {

  private final PrinterService printerService;
  private final PrintAgentConfig printAgentConfig;
  private final RabbitManagementService rabbitManagementService;
  private final RestTemplate restTemplate = new RestTemplate();

  @org.springframework.beans.factory.annotation.Value("${app.manager.url:http://localhost:8080}")
  private String managerUrl;

  public SetupController(
      PrinterService printerService,
      PrintAgentConfig printAgentConfig,
      RabbitManagementService rabbitManagementService) {
    this.printerService = printerService;
    this.printAgentConfig = printAgentConfig;
    this.rabbitManagementService = rabbitManagementService;
  }

  @GetMapping("/")
  public String setup(Model model) {
    List<String> printers = printerService.getAvailablePrinters();
    model.addAttribute("printers", printers);
    model.addAttribute("connected", printAgentConfig.isConnected());
    model.addAttribute("configToken", printAgentConfig.getToken());
    model.addAttribute("selectedPrinter", printAgentConfig.getPrinter());
    model.addAttribute(
        "queueName",
        printAgentConfig.getQueueName() != null ? printAgentConfig.getQueueName() : "Aguardando conexão...");
    model.addAttribute(
        "rabbitHost", printAgentConfig.getRabbitHost() != null ? printAgentConfig.getRabbitHost() : "-");
    return "setup";
  }

  @PostMapping("/test-print")
  public String testPrint(@RequestParam String printer, RedirectAttributes redirectAttributes) {
    try {
      printerService.printTest(printer);
      redirectAttributes.addFlashAttribute("success", "Teste de impressão enviado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erro ao imprimir: " + e.getMessage());
    }
    return "redirect:/";
  }

  @PostMapping("/configure")
  public String configure(
      @RequestParam String token,
      @RequestParam String printer,
      RedirectAttributes redirectAttributes) {
    try {
      String url = managerUrl + "/api/print-agent/activate";
      var request = Map.of("token", token);

      var response = restTemplate.postForEntity(url, request, Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        Map<String, Object> body = response.getBody();

        printAgentConfig.setToken(token);
        printAgentConfig.setPrinter(printer);
        printAgentConfig.setRestaurantId(body.get("restaurantId").toString());
        printAgentConfig.setRestaurantName(body.get("restaurantName").toString());
        printAgentConfig.setRabbitHost(body.get("rabbitHost").toString());
        printAgentConfig.setRabbitUsername(body.get("rabbitUsername").toString());
        printAgentConfig.setRabbitPassword(body.get("rabbitPassword").toString());
        printAgentConfig.setQueueName(body.get("queueName").toString());
        printAgentConfig.setConnected(true);

        // Inicia a conexão RabbitMQ dinamicamente
        rabbitManagementService.startConnection();

        redirectAttributes.addFlashAttribute(
            "success", "Agente ativado com sucesso para " + printAgentConfig.getRestaurantName());
      } else {
        redirectAttributes.addFlashAttribute("error", "Token inválido ou erro no servidor.");
      }
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute(
          "error", "Erro ao conectar com o servidor: " + e.getMessage());
    }
    return "redirect:/";
  }
}
