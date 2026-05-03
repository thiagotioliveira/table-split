package dev.thiagooliveira.tablesplit.infrastructure.ai;

public interface AiClient {
  String chat(String system, String user);
}
