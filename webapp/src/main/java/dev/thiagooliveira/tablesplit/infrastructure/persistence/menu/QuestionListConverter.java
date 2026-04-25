package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class QuestionListConverter
    implements AttributeConverter<Map<Language, List<ItemQuestion>>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<Language, List<ItemQuestion>> attribute) {
    if (attribute == null) return null;
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException(
          "Erro ao converter Map<Language, List<ItemQuestion>> para JSON", e);
    }
  }

  @Override
  public Map<Language, List<ItemQuestion>> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return new HashMap<>();
    try {
      return objectMapper.readValue(
          dbData, new TypeReference<Map<Language, List<ItemQuestion>>>() {});
    } catch (Exception e) {
      throw new RuntimeException(
          "Erro ao converter JSON para Map<Language, List<ItemQuestion>>", e);
    }
  }
}
