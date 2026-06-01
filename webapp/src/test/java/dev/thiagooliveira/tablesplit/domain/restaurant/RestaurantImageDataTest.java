package dev.thiagooliveira.tablesplit.domain.restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RestaurantImageDataTest {

  @Test
  @SuppressWarnings("java:S5785")
  void shouldVerifyRecordPropertiesEqualsHashCodeAndToString() {
    byte[] content = new byte[] {1, 2, 3};
    RestaurantImageData data1 = new RestaurantImageData("file.png", "image/png", content);
    RestaurantImageData data2 = new RestaurantImageData("file.png", "image/png", content);
    RestaurantImageData data3 = new RestaurantImageData("other.png", "image/png", content);

    assertEquals("file.png", data1.fileName());
    assertEquals("image/png", data1.contentType());
    assertArrayEquals(content, data1.content());

    assertTrue(data1.equals(data1));
    assertEquals(data1, data2);
    assertNotEquals(data1, data3);
    assertFalse(data1.equals(null));
    assertFalse(data1.equals(new Object()));

    RestaurantImageData diffFileName = new RestaurantImageData("diff.png", "image/png", content);
    RestaurantImageData diffContentType =
        new RestaurantImageData("file.png", "image/jpeg", content);
    RestaurantImageData diffContent =
        new RestaurantImageData("file.png", "image/png", new byte[] {9, 9});
    assertFalse(data1.equals(diffFileName));
    assertFalse(data1.equals(diffContentType));
    assertFalse(data1.equals(diffContent));

    assertEquals(data2.hashCode(), data1.hashCode());
    assertNotEquals(data3.hashCode(), data1.hashCode());

    assertNotNull(data1.toString());
  }
}
