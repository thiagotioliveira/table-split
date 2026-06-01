package dev.thiagooliveira.tablesplit.domain.menu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemTagTest {

  @Test
  void shouldVerifyAllEnumValues() {
    for (ItemTag tag : ItemTag.values()) {
      assertNotNull(tag);
      assertEquals(tag, ItemTag.valueOf(tag.name()));
    }
  }
}
