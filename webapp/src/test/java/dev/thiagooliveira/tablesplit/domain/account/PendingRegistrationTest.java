package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PendingRegistrationTest {

  @Test
  void shouldVerifyPropertiesAndExpiration() {
    UUID id = UUID.randomUUID();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
    PendingRegistration reg =
        new PendingRegistration(id, "test@email.com", "123456", "data", expiresAt);

    assertEquals(id, reg.getId());
    assertEquals("test@email.com", reg.getEmail());
    assertEquals("123456", reg.getCode());
    assertEquals("data", reg.getRegistrationData());
    assertEquals(expiresAt, reg.getExpiresAt());
    assertEquals("PT", reg.getLanguage());
    assertFalse(reg.isExpired());

    PendingRegistration reg2 =
        new PendingRegistration(
            id, "test@email.com", "123456", "data", expiresAt.minusHours(2), "EN");
    assertEquals("EN", reg2.getLanguage());
    assertTrue(reg2.isExpired());

    PendingRegistration reg3 = new PendingRegistration();
    reg3.setId(id);
    reg3.setEmail("email");
    reg3.setCode("code");
    reg3.setRegistrationData("regdata");
    reg3.setExpiresAt(expiresAt);
    reg3.setLanguage("FR");

    assertEquals(id, reg3.getId());
    assertEquals("email", reg3.getEmail());
    assertEquals("code", reg3.getCode());
    assertEquals("regdata", reg3.getRegistrationData());
    assertEquals(expiresAt, reg3.getExpiresAt());
    assertEquals("FR", reg3.getLanguage());
  }
}
