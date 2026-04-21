package dev.thiagooliveira.tablesplit.agent.controller;

import dev.thiagooliveira.tablesplit.agent.config.AgentConfig;
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
  private final AgentConfig agentConfig;
  private final RabbitManagementService rabbitManagementService;
  private final RestTemplate restTemplate = new RestTemplate();

  @org.springframework.beans.factory.annotation.Value("${app.manager.url:http://localhost:8080}")
  private String managerUrl;

  public SetupController(
      PrinterService printerService,
      AgentConfig agentConfig,
      RabbitManagementService rabbitManagementService) {
    this.printerService = printerService;
    this.agentConfig = agentConfig;
    this.rabbitManagementService = rabbitManagementService;
  }

  @GetMapping("/")
  public String setup(Model model) {
    List<String> printers = printerService.getAvailablePrinters();
    model.addAttribute("printers", printers);
    model.addAttribute("connected", agentConfig.isConnected());
    model.addAttribute("configToken", agentConfig.getToken());
    model.addAttribute("selectedPrinter", agentConfig.getPrinter());
    model.addAttribute(
        "queueName",
        agentConfig.getQueueName() != null ? agentConfig.getQueueName() : "Aguardando conexão...");
    model.addAttribute(
        "rabbitHost", agentConfig.getRabbitHost() != null ? agentConfig.getRabbitHost() : "-");
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

        agentConfig.setToken(token);
        agentConfig.setPrinter(printer);
        agentConfig.setRestaurantId(body.get("restaurantId").toString());
        agentConfig.setRestaurantName(body.get("restaurantName").toString());
        agentConfig.setRabbitHost(body.get("rabbitHost").toString());
        agentConfig.setRabbitUsername(body.get("rabbitUsername").toString());
        agentConfig.setRabbitPassword(body.get("rabbitPassword").toString());
        agentConfig.setQueueName(body.get("queueName").toString());
        agentConfig.setConnected(true);

        // Inicia a conexão RabbitMQ dinamicamente
        rabbitManagementService.startConnection();

        redirectAttributes.addFlashAttribute(
            "success", "Agente ativado com sucesso para " + agentConfig.getRestaurantName());
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
