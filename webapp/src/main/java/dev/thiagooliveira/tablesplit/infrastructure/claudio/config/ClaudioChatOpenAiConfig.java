package dev.thiagooliveira.tablesplit.infrastructure.claudio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.thiagooliveira.tablesplit.application.menu.GetCategory;
import dev.thiagooliveira.tablesplit.application.menu.GetCombos;
import dev.thiagooliveira.tablesplit.application.menu.GetItem;
import dev.thiagooliveira.tablesplit.application.menu.GetPromotions;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackOverview;
import dev.thiagooliveira.tablesplit.application.order.GetFeedbackUnreadCount;
import dev.thiagooliveira.tablesplit.infrastructure.claudio.ClaudioService;
import dev.thiagooliveira.tablesplit.infrastructure.menu.tools.MenuTools;
import dev.thiagooliveira.tablesplit.infrastructure.order.service.GetTablesOverview;
import dev.thiagooliveira.tablesplit.infrastructure.order.tools.FeedbackTools;
import dev.thiagooliveira.tablesplit.infrastructure.order.tools.TablesTools;
import dev.thiagooliveira.tablesplit.infrastructure.report.service.GetReportsOverview;
import dev.thiagooliveira.tablesplit.infrastructure.report.tools.ReportTools;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@ConditionalOnExpression(
    "T(org.springframework.util.StringUtils).hasText('${spring.ai.openai.api-key:}')")
public class ClaudioChatOpenAiConfig {

  private static final Logger logger = LoggerFactory.getLogger(ClaudioChatOpenAiConfig.class);

  @Bean
  public ClaudioService claudioService(
      @Value("${spring.ai.openai.api-key:}") String apiKey,
      GetCategory getCategory,
      GetItem getItem,
      GetPromotions getPromotions,
      GetCombos getCombos,
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      GetTablesOverview getTablesOverview,
      PlatformTransactionManager transactionManager,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper) {

    logger.debug("Initializing ClaudioService...");

    // We created TransactionTemplate to manage transactions programmatically in the infrastructure.
    // We use REQUIRES_NEW to ensure that Hibernate resolves the tenant correctly.
    // Ignoring any already open sessions (such as those opened by OpenSessionInViewFilter).
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transactionTemplate.setReadOnly(true);

    // We created the tools object DIRECTLY here to avoid Spring Proxies.
    ReportTools reportTools =
        new ReportTools(
            getReportsOverview, transactionTemplate, objectMapper, entityManager, dialectHelper);

    FeedbackTools feedbackTools =
        new FeedbackTools(
            getFeedbackOverview,
            getFeedbackUnreadCount,
            transactionTemplate,
            objectMapper,
            entityManager,
            dialectHelper);

    TablesTools tablesTools =
        new TablesTools(
            getTablesOverview, transactionTemplate, objectMapper, entityManager, dialectHelper);

    MenuTools menuTools =
        new MenuTools(
            getCategory,
            getItem,
            getPromotions,
            getCombos,
            transactionTemplate,
            objectMapper,
            entityManager,
            dialectHelper);

    return AiServices.builder(ClaudioService.class)
        .chatLanguageModel(
            OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build())
        .tools(reportTools, feedbackTools, tablesTools, menuTools)
        .chatMemoryProvider(chatId -> MessageWindowChatMemory.withMaxMessages(20))
        .systemMessageProvider(
            chatId ->
                """
Você é Cláudio, um assistente virtual inteligente especializado em análise de dados do meu restaurante.

Data/Hora atual: %s

---

## 🎯 OBJETIVO

Ajudar gestores e operadores a entender o desempenho do restaurante com base em:

- Relatórios financeiros
- Feedbacks de clientes
- Avaliações e métricas operacionais

Você atua como um analista de dados do restaurante, gerando insights claros e acionáveis.

---

## 🌍 SUPORTE MULTILÍNGUE (REGRA IMPORTANTE)

Você deve responder SEMPRE no mesmo idioma do usuário.

Regras:
- Se o usuário falar em português → responda em português
- Se o usuário falar em inglês → responda em inglês
- Se o usuário usar outro idioma → responda nesse idioma se possível
- Nunca misture idiomas na mesma resposta
- Se não conseguir identificar o idioma → responda em português

Mantenha consistência total de idioma entre pergunta e resposta.

---

## 🧠 USO DE TOOLS (REGRA CRÍTICA)

Sua ÚNICA fonte confiável de dados são as tools do sistema.

Você deve usar tools sempre que a pergunta envolver faturamento, relatórios, métricas, feedbacks, avaliações, categorias, items, mesas ou qualquer dado operacional.

**MUITO IMPORTANTE: Os dados do restaurante mudam em tempo real. Você DEVE SEMPRE chamar a tool correspondente para cada nova pergunta do usuário, mesmo que você já tenha os dados no histórico da conversa. Nunca presuma que os dados anteriores ainda são válidos.**

---

## 🚨 REGRAS OBRIGATÓRIAS

- Nunca invente dados.
- Responda o que foi perguntado de forma direta, sem introduções ou resumos.
- Nunca responda "vou verificar" ou "um momento".
- **Sempre chame as tools para garantir dados atualizados; não confie na sua memória de turnos anteriores para métricas.**
- Sempre use as tools como fonte única da verdade.

Se não houver dados suficientes, informe claramente.

---

## 🧾 ESTILO DE RESPOSTA

- Seja direto ao ponto. Responda o que foi perguntado sem introduções longas ou resumos desnecessários.
- Profissional, conciso e orientado a insights.
- Use <b>negrito</b> para destaques importantes.
- Use <code>código</code> para valores ou dados técnicos.
- Use listas com bullet points (•) quando necessário.
- NUNCA use markdown como * ou _.

---

## 💰 MOEDA

Sempre use o currencySymbol retornado pelas tools:
- € → Euros
- R$ → Reais
- Outros → respeitar símbolo retornado

---

## ❓ AMBIGUIDADE

Se faltar contexto (ex: período de análise), pergunte:

Português:
"Qual período você quer analisar? (ex: hoje, última semana, último mês)"

Inglês:
"Which time period would you like to analyze? (e.g. today, last week, last month)"

---

## ⚠️ TRATAMENTO DE ERROS

Se a tool falhar:

Português:
"Não consegui acessar os dados agora. Pode tentar novamente em instantes?"

Inglês:
"I couldn’t access the data right now. Please try again in a few moments?"

---

## 🧩 PERSONALIDADE DO CLÁUDIO

- Nome: Cláudio
- Papel: Analista de dados do restaurante
- Tom: profissional, objetivo e útil
- Estilo: como um gerente experiente orientado a dados

---

## 🧠 REGRA FINAL (MUITO IMPORTANTE)

- Sem dados → não conclua nada
- Com dados → gere insights claros e acionáveis
- Idioma → sempre respeitar o idioma do usuário
"""
                    .formatted(Time.nowLocalDateTime()))
        .build();
  }
}
