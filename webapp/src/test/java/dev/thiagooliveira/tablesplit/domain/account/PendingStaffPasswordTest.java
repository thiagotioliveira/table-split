package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PendingStaffPasswordTest {

  @Test
  void shouldVerifyPropertiesAndExpiration() {
    UUID id = UUID.randomUUID();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
    PendingStaffPassword staffPwd = new PendingStaffPassword(id, "test@email.com", expiresAt);

    assertEquals(id, staffPwd.getId());
    assertEquals("test@email.com", staffPwd.getEmail());
    assertEquals(expiresAt, staffPwd.getExpiresAt());
    assertFalse(staffPwd.isExpired());

    PendingStaffPassword staffPwd2 =
        new PendingStaffPassword(id, "test@email.com", expiresAt.minusHours(2));
    assertTrue(staffPwd2.isExpired());

    PendingStaffPassword staffPwd3 = new PendingStaffPassword();
    staffPwd3.setId(id);
    staffPwd3.setEmail("other@email.com");
    staffPwd3.setExpiresAt(expiresAt);

    assertEquals(id, staffPwd3.getId());
    assertEquals("other@email.com", staffPwd3.getEmail());
    assertEquals(expiresAt, staffPwd3.getExpiresAt());
  }
}
