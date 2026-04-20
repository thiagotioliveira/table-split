package dev.thiagooliveira.tablesplit.agent.controller;

import dev.thiagooliveira.tablesplit.agent.service.PrinterService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SetupController {

  private final PrinterService printerService;
  private final dev.thiagooliveira.tablesplit.agent.config.AgentConfig agentConfig;
  private final org.springframework.web.client.RestTemplate restTemplate =
      new org.springframework.web.client.RestTemplate();

  @org.springframework.beans.factory.annotation.Value("${app.manager.url:http://localhost:8080}")
  private String managerUrl;

  public SetupController(
      PrinterService printerService,
      dev.thiagooliveira.tablesplit.agent.config.AgentConfig agentConfig) {
    this.printerService = printerService;
    this.agentConfig = agentConfig;
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
        agentConfig.getRestaurantId() != null
            ? "restaurant." + agentConfig.getRestaurantId() + ".queue"
            : "Aguardando conexão...");
    model.addAttribute("rabbitHost", "localhost");
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
      String url = managerUrl + "/api/v1/agent/activate";
      var request = new java.util.HashMap<String, String>();
      request.put("token", token);

      var response = restTemplate.postForEntity(url, request, java.util.Map.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        java.util.Map<String, Object> body = response.getBody();
        agentConfig.setToken(token);
        agentConfig.setPrinter(printer);
        agentConfig.setRestaurantId(body.get("restaurantId").toString());
        agentConfig.setRestaurantName(body.get("restaurantName").toString());
        agentConfig.setConnected(true);

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
