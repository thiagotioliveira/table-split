package dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant;

import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class CuisineTypeJsonListConverter implements AttributeConverter<List<CuisineType>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<CuisineType> list) {
    try {
      if (list == null || list.isEmpty()) {
        return "[]";
      }
      return mapper.writeValueAsString(list);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting list to JSON.", e);
    }
  }

  @Override
  public List<CuisineType> convertToEntityAttribute(String s) {
    try {
      if (s == null || s.isBlank()) {
        return Collections.emptyList();
      }
      return mapper.readValue(s, new TypeReference<List<CuisineType>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting JSON to list.", e);
    }
  }
}
