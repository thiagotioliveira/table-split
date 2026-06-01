package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.account.command.UpdateStaffCommand;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.account.Staff;
import dev.thiagooliveira.tablesplit.domain.account.StaffRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateStaffTest {

  private StaffRepository staffRepository;
  private UpdateStaff updateStaff;

  @BeforeEach
  void setUp() {
    staffRepository = mock(StaffRepository.class);
    updateStaff = new UpdateStaff(staffRepository);
  }

  @Test
  void shouldUpdateStaffSuccessfully() {
    UUID staffId = UUID.randomUUID();
    Staff staff = new Staff();
    staff.setId(staffId);
    staff.setEmail("staff@example.com");
    staff.setModules(Set.of());

    var command =
        new UpdateStaffCommand(
            staffId,
            "John",
            "Doe",
            "staff@example.com",
            "+1234567890",
            null,
            true,
            Set.of(Module.ORDERS));

    when(staffRepository.findById(staffId)).thenReturn(Optional.of(staff));

    Staff result = updateStaff.execute(command);

    assertNotNull(result);
    verify(staffRepository).findById(staffId);
    verify(staffRepository).save(staff);
  }

  @Test
  void shouldThrowWhenStaffNotFound() {
    UUID staffId = UUID.randomUUID();
    var command =
        new UpdateStaffCommand(staffId, "John", "Doe", "x@x.com", null, null, true, Set.of());

    when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> updateStaff.execute(command));
  }
}
