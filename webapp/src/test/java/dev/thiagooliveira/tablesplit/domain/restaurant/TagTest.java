package dev.thiagooliveira.tablesplit.domain.restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TagTest {

  @Test
  void shouldVerifyAllEnumValues() {
    for (Tag tag : Tag.values()) {
      assertNotNull(tag);
      assertEquals(tag, Tag.valueOf(tag.name()));
    }
  }
}
