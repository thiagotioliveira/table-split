package dev.thiagooliveira.tablesplit.infrastructure.claudio;

import dev.langchain4j.service.UserMessage;

public interface LanguageDetector {
  @UserMessage(
      "Detect the language of the following text. Return ONLY 'PT' if it is Portuguese or 'EN' if it is English. If you are unsure or it is any other language, return 'PT'. Text: {{it}}")
  String detect(String text);
}
