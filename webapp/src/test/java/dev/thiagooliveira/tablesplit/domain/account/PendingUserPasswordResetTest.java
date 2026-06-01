package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PendingUserPasswordResetTest {

  @Test
  void shouldVerifyPropertiesAndExpiration() {
    UUID id = UUID.randomUUID();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
    PendingUserPasswordReset reset = new PendingUserPasswordReset(id, "test@email.com", expiresAt);

    assertEquals(id, reset.getId());
    assertEquals("test@email.com", reset.getEmail());
    assertEquals(expiresAt, reset.getExpiresAt());
    assertFalse(reset.isExpired());

    PendingUserPasswordReset reset2 =
        new PendingUserPasswordReset(id, "test@email.com", expiresAt.minusHours(2));
    assertTrue(reset2.isExpired());

    PendingUserPasswordReset reset3 = new PendingUserPasswordReset();
    reset3.setId(id);
    reset3.setEmail("other@email.com");
    reset3.setExpiresAt(expiresAt);

    assertEquals(id, reset3.getId());
    assertEquals("other@email.com", reset3.getEmail());
    assertEquals(expiresAt, reset3.getExpiresAt());
  }
}
