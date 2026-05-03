package dev.thiagooliveira.tablesplit.infrastructure.config.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.application.report.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ChatAiService;
import dev.thiagooliveira.tablesplit.infrastructure.ai.ReportAndFeedbackTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${spring.ai.openai.api-key:}')")
public class OpenAiConfig {

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
    return AiServices.builder(ChatAiService.class)
        .chatLanguageModel(
            OpenAiChatModel.builder().apiKey(apiKey).modelName("gpt-4o-mini").build())
        .tools(reportAndFeedbackTools)
        .chatMemoryProvider(chatId -> MessageWindowChatMemory.withMaxMessages(10))
        .systemMessageProvider(
            chatId ->
                "Você é o assistente inteligente do Table Split para gestores de restaurantes. "
                    + "Sua missão é fornecer informações sobre relatórios de vendas e feedback de clientes. "
                    + "Sempre que um gestor perguntar algo, use as ferramentas disponíveis para buscar os dados em tempo real. "
                    + "Seja profissional, prestativo e forneça resumos claros.")
        .build();
  }
}
