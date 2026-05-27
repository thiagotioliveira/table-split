package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellation;
import dev.thiagooliveira.tablesplit.domain.account.PendingAccountCancellationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Role;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.PendingAccountCancellationCreatedEvent;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class RequestAccountCancellationTest {

  @Mock private UserRepository userRepository;

  @Mock private PendingAccountCancellationRepository pendingAccountCancellationRepository;

  @Mock private ApplicationEventPublisher eventPublisher;

  private RequestAccountCancellation requestAccountCancellation;

  @BeforeEach
  void setUp() {
    requestAccountCancellation =
        new RequestAccountCancellation(
            userRepository, pendingAccountCancellationRepository, eventPublisher);
  }

  @Test
  void execute_shouldRequestCancellationSuccessfully() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    String restaurantName = "Burger Joint";
    String baseUrl = "http://localhost:8080";

    User adminUser = new User();
    adminUser.setEmail("admin@burger.com");
    adminUser.setFirstName("John");
    adminUser.setLanguage(Language.EN);
    adminUser.setRole(Role.RESTAURANT_ADMIN);

    when(userRepository.findByAccountId(accountId)).thenReturn(List.of(adminUser));

    // Act
    requestAccountCancellation.execute(accountId, restaurantName, baseUrl);

    // Assert
    ArgumentCaptor<PendingAccountCancellation> cancellationCaptor =
        ArgumentCaptor.forClass(PendingAccountCancellation.class);
    verify(pendingAccountCancellationRepository, times(1)).save(cancellationCaptor.capture());

    PendingAccountCancellation saved = cancellationCaptor.getValue();
    assertEquals(accountId, saved.getAccountId());
    assertNotNull(saved.getCode());
    assertEquals(6, saved.getCode().length());
    assertNotNull(saved.getExpiresAt());

    ArgumentCaptor<PendingAccountCancellationCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(PendingAccountCancellationCreatedEvent.class);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

    PendingAccountCancellationCreatedEvent event = eventCaptor.getValue();
    assertEquals("admin@burger.com", event.email());
    assertEquals(saved.getCode(), event.code());
    assertEquals("John", event.firstName());
    assertEquals("EN", event.language());
    assertEquals(baseUrl, event.baseUrl());
    assertEquals(restaurantName, event.restaurantName());
  }

  @Test
  void execute_shouldThrowException_whenAdminUserNotFound() {
    // Arrange
    UUID accountId = UUID.randomUUID();
    when(userRepository.findByAccountId(accountId)).thenReturn(List.of());

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> requestAccountCancellation.execute(accountId, "Burger Joint", "http://localhost"));

    verify(pendingAccountCancellationRepository, never()).save(any());
    verify(eventPublisher, never()).publishEvent(any());
  }
}
