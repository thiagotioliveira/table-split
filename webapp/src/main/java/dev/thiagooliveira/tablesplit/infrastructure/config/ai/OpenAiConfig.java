package dev.thiagooliveira.tablesplit.infrastructure.config.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ReportAndFeedbackTools;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${spring.ai.openai.api-key:}')")
public class OpenAiConfig {

  private static final Logger logger = LoggerFactory.getLogger(OpenAiConfig.class);

  @Bean
  public ReportAndFeedbackTools reportAndFeedbackTools(
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount) {
    return new ReportAndFeedbackTools(
        getReportsOverview, getFeedbackOverview, getFeedbackUnreadCount);
  }

  @Bean
  public ChatAiService chatAiService(
      @Value("${spring.ai.openai.api-key:}") String apiKey,
      ReportAndFeedbackTools reportAndFeedbackTools) {

    logger.info("Inicializando ChatAiService com ferramentas de relatório...");

    return AiServices.builder(ChatAiService.class)
        .chatLanguageModel(
            OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build())
        .tools(reportAndFeedbackTools)
        .chatMemoryProvider(chatId -> MessageWindowChatMemory.withMaxMessages(20))
        .systemMessageProvider(
            chatId ->
                "Você é o assistente inteligente do Table Split para gestores de restaurantes. "
                    + "Data/Hora atual: "
                    + Time.nowLocalDateTime()
                    + ". "
                    + "Você NÃO tem acesso interno a dados de faturamento ou feedback. "
                    + "Sua ÚNICA fonte de dados são as ferramentas (tools). "
                    + "Sempre que o gestor perguntar algo, use a ferramenta apropriada e responda com base nos dados reais. "
                    + "Nunca diga que 'vai verificar' sem de fato chamar a ferramenta. "
                    + "Responda em Português, de forma profissional e use o símbolo monetário correto retornado pelo relatório.")
        .build();
  }
}
