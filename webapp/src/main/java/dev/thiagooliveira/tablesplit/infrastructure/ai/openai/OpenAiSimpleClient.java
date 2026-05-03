package dev.thiagooliveira.tablesplit.infrastructure.ai.openai;

import dev.thiagooliveira.tablesplit.infrastructure.ai.AiClient;
import java.util.List;
import org.springframework.web.client.RestClient;

public class OpenAiSimpleClient implements AiClient {

  private final RestClient restClient;
  private final String apiKey;

  public OpenAiSimpleClient(String apiKey) {
    this.restClient = RestClient.builder().baseUrl("https://api.openai.com/v1").build();
    this.apiKey = apiKey;
  }

  public String chat(String system, String user) {
    OpenAiRequest request =
        new OpenAiRequest(
            "gpt-4o-mini", List.of(new Message("system", system), new Message("user", user)));

    OpenAiResponse response =
        restClient
            .post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .body(request)
            .retrieve()
            .body(OpenAiResponse.class);

    if (response != null && response.choices() != null && !response.choices().isEmpty()) {
      return response.choices().get(0).message().content();
    }
    return "Desculpe, não consegui obter uma resposta da IA.";
  }

  public record OpenAiRequest(String model, List<Message> messages) {}

  public record Message(String role, String content) {}

  public record OpenAiResponse(List<Choice> choices) {
    public record Choice(Message message) {}
  }
}
