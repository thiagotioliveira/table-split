package dev.thiagooliveira.tablesplit.application.account;

import java.util.List;

public interface UserSessionInvalidator {
  void invalidateSessionsForEmails(List<String> emails);
}
