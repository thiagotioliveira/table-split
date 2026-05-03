package dev.thiagooliveira.tablesplit.infrastructure.config.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ReportAndFeedbackTools;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.lang.reflect.Method;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${spring.ai.openai.api-key:}')")
public class OpenAiConfig {

  private static final Logger logger = LoggerFactory.getLogger(OpenAiConfig.class);

  @Bean
  public ChatAiService chatAiService(
      @Value("${spring.ai.openai.api-key:}") String apiKey,
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      PlatformTransactionManager transactionManager,
      ObjectMapper objectMapper) {

    logger.info("Inicializando ChatAiService...");

    // Criamos o TransactionTemplate para gerenciar transações programaticamente na infra
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setReadOnly(true);

    // Criamos o objeto de ferramentas DIRETAMENTE aqui para evitar Proxies do Spring
    ReportAndFeedbackTools tools =
        new ReportAndFeedbackTools(
            getReportsOverview,
            getFeedbackOverview,
            getFeedbackUnreadCount,
            transactionTemplate,
            objectMapper);

    // Log de diagnóstico para confirmar detecção
    for (Method method : tools.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Tool.class)) {
        logger.info("-> Ferramenta registrada com sucesso: {}", method.getName());
      }
    }

    return AiServices.builder(ChatAiService.class)
        .chatLanguageModel(
            OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build())
        .tools(tools)
        .chatMemoryProvider(chatId -> MessageWindowChatMemory.withMaxMessages(20))
        .systemMessageProvider(
            chatId ->
                "Você é o assistente inteligente do Table Split. "
                    + "Data/Hora atual: "
                    + Time.nowLocalDateTime()
                    + ". "
                    + "Sua ÚNICA fonte de dados financeiros são as ferramentas (tools). "
                    + "Ao receber uma pergunta sobre faturamento ou feedback, você DEVE chamar a ferramenta IMEDIATAMENTE. "
                    + "NUNCA responda que 'vai verificar' ou 'um momento'. Responda apenas quando tiver os dados da ferramenta. "
                    + "Use o símbolo monetário (currencySymbol) retornado pela ferramenta. "
                    + "Se o símbolo for '€', use Euros. Se for 'R$', use Reais. "
                    + "Responda sempre em Português de forma concisa.")
        .build();
  }
}
