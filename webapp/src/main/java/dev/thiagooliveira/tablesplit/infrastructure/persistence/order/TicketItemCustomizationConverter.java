package dev.thiagooliveira.tablesplit.infrastructure.persistence.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.domain.order.TicketItemCustomization;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class TicketItemCustomizationConverter
    implements AttributeConverter<List<TicketItemCustomization>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<TicketItemCustomization> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting List<TicketItemCustomization> to JSON", e);
    }
  }

  @Override
  public List<TicketItemCustomization> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return objectMapper.readValue(dbData, new TypeReference<List<TicketItemCustomization>>() {});
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to List<TicketItemCustomization>", e);
    }
  }
}
