package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.domain.restaurant.Language;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class LanguageJsonListConverter implements AttributeConverter<List<Language>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<Language> tags) {
    try {
      if (tags == null || tags.isEmpty()) {
        return "[]";
      }
      return mapper.writeValueAsString(tags);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting list to JSON.", e);
    }
  }

  @Override
  public List<Language> convertToEntityAttribute(String s) {
    try {
      if (s == null || s.isBlank()) {
        return Collections.emptyList();
      }
      return mapper.readValue(s, new TypeReference<List<Language>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting JSON to list.", e);
    }
  }
}
