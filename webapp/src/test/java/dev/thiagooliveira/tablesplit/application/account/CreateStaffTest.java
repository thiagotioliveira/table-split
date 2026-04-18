package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.application.account.command.CreateStaffCommand;
import dev.thiagooliveira.tablesplit.application.exception.PlanLimitExceededException;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateStaffTest {

  @Mock private StaffRepository staffRepository;
  @Mock private UserRepository userRepository;
  @Mock private RestaurantRepository restaurantRepository;
  @Mock private PlanLimitValidator planLimitValidator;

  private CreateStaff createStaff;

  @BeforeEach
  void setUp() {
    createStaff =
        new CreateStaff(staffRepository, userRepository, restaurantRepository, planLimitValidator);
  }

  @Test
  void shouldThrowException_whenLimitReached() {
    UUID restaurantId = UUID.randomUUID();
    CreateStaffCommand command =
        new CreateStaffCommand(
            restaurantId, "Staff", "Name", "staff@test.com", "123", "pass", Language.PT, Set.of());

    when(staffRepository.count(restaurantId)).thenReturn(3L);
    doThrow(new PlanLimitExceededException("error.plan.limit.staff"))
        .when(planLimitValidator)
        .validateByRestaurantId(eq(restaurantId), any(), eq(3L));

    assertThrows(PlanLimitExceededException.class, () -> createStaff.execute(command));
    verify(staffRepository, never()).save(any());
  }

  @Test
  void shouldAllowCreation_whenUnderLimit() {
    UUID restaurantId = UUID.randomUUID();
    CreateStaffCommand command =
        new CreateStaffCommand(
            restaurantId, "Staff", "Name", "staff@test.com", "123", "pass", Language.PT, Set.of());

    dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant restaurant =
        new dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant();
    restaurant.setAccountId(UUID.randomUUID());

    when(staffRepository.count(restaurantId)).thenReturn(2L);
    when(restaurantRepository.findById(restaurantId)).thenReturn(java.util.Optional.of(restaurant));
    when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.empty());
    when(staffRepository.findByEmail(any())).thenReturn(java.util.Optional.empty());

    createStaff.execute(command);

    verify(staffRepository).save(any());
    verify(planLimitValidator).validateByRestaurantId(eq(restaurantId), any(), eq(2L));
  }
}
