package dev.thiagooliveira.tablesplit.infrastructure.web.security;

import dev.thiagooliveira.tablesplit.application.account.UserSessionInvalidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SpringSessionInvalidator implements UserSessionInvalidator {

  private final SessionRegistry sessionRegistry;

  public SpringSessionInvalidator(SessionRegistry sessionRegistry) {
    this.sessionRegistry = sessionRegistry;
  }

  @Override
  public void invalidateSessionsForEmails(List<String> emails) {
    if (emails == null || emails.isEmpty()) {
      return;
    }

    List<String> normalizedEmails =
        emails.stream().map(email -> email.trim().toLowerCase()).collect(Collectors.toList());

    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (principal instanceof UserDetails userDetails) {
        String username = userDetails.getUsername();
        if (username != null && normalizedEmails.contains(username.trim().toLowerCase())) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          for (SessionInformation session : sessions) {
            session.expireNow();
          }
        }
      }
    }
  }
}
