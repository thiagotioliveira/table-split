package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class LanguageMapConverter implements AttributeConverter<Map<Language, String>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<Language, String> attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao converter Map para JSON", e);
    }
  }

  @Override
  public Map<Language, String> convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, new TypeReference<Map<Language, String>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Erro ao converter JSON para Map", e);
    }
  }
}
