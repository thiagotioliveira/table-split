package dev.thiagooliveira.tablesplit.infrastructure.claudio;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface ClaudioService {
  String chat(@MemoryId Long chatId, @UserMessage String userMessage);
}
