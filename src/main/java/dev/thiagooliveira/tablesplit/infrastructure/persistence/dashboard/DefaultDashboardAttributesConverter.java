package dev.thiagooliveira.tablesplit.infrastructure.persistence.dashboard;

import dev.thiagooliveira.tablesplit.domain.dashboard.v1.DefaultDashboardAttributes;
import jakarta.persistence.AttributeConverter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class DefaultDashboardAttributesConverter
    implements AttributeConverter<DefaultDashboardAttributes, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(DefaultDashboardAttributes defaultDashboardAttributes) {
    try {
      if (defaultDashboardAttributes == null) {
        return "";
      }
      return mapper.writeValueAsString(defaultDashboardAttributes);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting attributes to JSON.", e);
    }
  }

  @Override
  public DefaultDashboardAttributes convertToEntityAttribute(String s) {
    try {
      if (s == null || s.isBlank()) {
        return new DefaultDashboardAttributes();
      }
      return mapper.readValue(s, new TypeReference<DefaultDashboardAttributes>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException("Error converting JSON to attributes.", e);
    }
  }
}
