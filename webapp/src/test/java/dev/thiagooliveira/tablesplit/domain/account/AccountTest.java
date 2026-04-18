package dev.thiagooliveira.tablesplit.domain.account;

import static org.junit.jupiter.api.Assertions.*;

import dev.thiagooliveira.tablesplit.domain.common.Time;
import org.junit.jupiter.api.Test;

class AccountTest {

  @Test
  void shouldReturnProfessionalPlan_whenInTrial() {
    Account account = new Account();
    account.setPlan(Plan.STARTER);
    account.setStatus(AccountStatus.TRIAL);
    account.setTrialEndsAt(Time.nowOffset().plusDays(1));

    assertTrue(account.isInTrial());
    assertEquals(Plan.PROFESSIONAL, account.getEffectivePlan());
  }

  @Test
  void shouldReturnOriginalPlan_whenTrialExpired() {
    Account account = new Account();
    account.setPlan(Plan.STARTER);
    account.setStatus(AccountStatus.TRIAL);
    account.setTrialEndsAt(Time.nowOffset().minusDays(1));

    assertFalse(account.isInTrial());
    assertEquals(Plan.STARTER, account.getEffectivePlan());
  }

  @Test
  void shouldReturnOriginalPlan_whenActiveButNotTrial() {
    Account account = new Account();
    account.setPlan(Plan.STARTER);
    account.setStatus(AccountStatus.ACTIVE);

    assertFalse(account.isInTrial());
    assertEquals(Plan.STARTER, account.getEffectivePlan());
  }
}
