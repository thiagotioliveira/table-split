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
import dev.thiagooliveira.tablesplit.infrastructure.tenant.DatabaseDialectHelper;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Method;
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
public class OpenAiConfig {

  private static final Logger logger = LoggerFactory.getLogger(OpenAiConfig.class);

  @Bean
  public ChatAiService chatAiService(
      @Value("${spring.ai.openai.api-key:}") String apiKey,
      GetReportsOverview getReportsOverview,
      GetFeedbackOverview getFeedbackOverview,
      GetFeedbackUnreadCount getFeedbackUnreadCount,
      PlatformTransactionManager transactionManager,
      ObjectMapper objectMapper,
      EntityManager entityManager,
      DatabaseDialectHelper dialectHelper) {

    logger.debug("Inicializando ChatAiService...");

    // Criamos o TransactionTemplate para gerenciar transações programaticamente na infra.
    // Usamos REQUIRES_NEW para garantir que o Hibernate resolva o Tenant corretamente,
    // ignorando qualquer sessão já aberta (como pelo OpenSessionInViewFilter).
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transactionTemplate.setReadOnly(true);

    // Criamos o objeto de ferramentas DIRETAMENTE aqui para evitar Proxies do Spring
    ReportAndFeedbackTools tools =
        new ReportAndFeedbackTools(
            getReportsOverview,
            getFeedbackOverview,
            getFeedbackUnreadCount,
            transactionTemplate,
            objectMapper,
            entityManager,
            dialectHelper);

    // Log de diagnóstico para confirmar detecção
    for (Method method : tools.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Tool.class)) {
        logger.debug("-> Ferramenta registrada com sucesso: {}", method.getName());
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

Você deve usar tools sempre que a pergunta envolver:
- faturamento
- relatórios
- métricas
- feedbacks de clientes
- avaliações
- desempenho do restaurante
- qualquer dado operacional

---

## 🚨 REGRAS OBRIGATÓRIAS

- Nunca invente dados
- Nunca responda "vou verificar" ou "um momento"
- Nunca responda sem usar tools quando dados forem necessários
- Sempre use os dados retornados pelas tools como fonte única da verdade

Se não houver dados suficientes:
- informe claramente
- ou peça mais detalhes ao usuário

---

## 📊 FORMATO DE RESPOSTA (ANÁLISE)

Quando responder com dados, sempre estruturar:

### 📊 Summary
(visão geral objetiva)

### 👍 Positive Points
- ...

### ⚠️ Issues / Attention Points
- ...

### 💡 Recommendations
- ações práticas e acionáveis

---

## 🧾 ESTILO DE RESPOSTA

- Claro e direto
- Profissional, mas acessível
- Use bullet points sempre que possível
- Evite textos longos
- Seja orientado a insights, não apenas dados

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
