package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.ItemOption;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class OptionListConverter implements AttributeConverter<List<ItemOption>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<ItemOption> attribute) {
    if (attribute == null) return null;
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException("Erro ao converter List<ItemOption> para JSON", e);
    }
  }

  @Override
  public List<ItemOption> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return new ArrayList<>();
    try {
      return objectMapper.readValue(dbData, new TypeReference<List<ItemOption>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Erro ao converter JSON para List<ItemOption>", e);
    }
  }
}
