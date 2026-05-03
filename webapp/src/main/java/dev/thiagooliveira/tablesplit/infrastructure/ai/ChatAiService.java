package dev.thiagooliveira.tablesplit.infrastructure.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ChatAiService {
  String chat(@MemoryId Long chatId, @UserMessage String userMessage);
}
