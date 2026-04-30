package dev.thiagooliveira.tablesplit.application.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.thiagooliveira.tablesplit.domain.account.Account;
import dev.thiagooliveira.tablesplit.domain.account.AccountRepository;
import dev.thiagooliveira.tablesplit.domain.account.AccountStatus;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.common.Time;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartTrialTest {

  @Mock private AccountRepository accountRepository;

  private StartTrial startTrial;

  @BeforeEach
  void setUp() {
    startTrial = new StartTrial(accountRepository);
  }

  @Test
  void shouldStartTrialSuccessfully() {
    UUID accountId = UUID.randomUUID();
    Account account = new Account();
    account.setId(accountId);
    account.setPlan(Plan.STARTER);
    account.setTrialStartedAt(null);

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    startTrial.execute(accountId);

    assertEquals(AccountStatus.TRIAL, account.getStatus());
    assertTrue(account.isTrialUsed());
    assertNotNull(account.getTrialStartedAt());
    assertNotNull(account.getTrialEndsAt());
    assertEquals(Plan.PROFESSIONAL, account.getEffectivePlan());
    verify(accountRepository).save(account);
  }

  @Test
  void shouldThrowException_whenTrialAlreadyUsed() {
    UUID accountId = UUID.randomUUID();
    Account account = new Account();
    account.setId(accountId);
    account.setTrialStartedAt(Time.nowOffset());

    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

    assertThrows(IllegalStateException.class, () -> startTrial.execute(accountId));
    verify(accountRepository, never()).save(any());
  }

  @Test
  void shouldThrowException_whenAccountNotFound() {
    UUID accountId = UUID.randomUUID();
    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> startTrial.execute(accountId));
  }
}
