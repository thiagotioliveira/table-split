package dev.thiagooliveira.tablesplit.domain.event;

import dev.thiagooliveira.tablesplit.domain.account.User;
import java.util.UUID;

public class UserUpdatedEvent implements DomainEvent<UserUpdatedEvent.UserCreatedEventDetails> {
  private final UUID accountId;
  private final UUID userId;
  private final UserCreatedEventDetails details;

  public UserUpdatedEvent(UUID accountId, User user) {
    this.accountId = accountId;
    this.userId = user.getId();
    this.details = new UserCreatedEventDetails(user);
  }

  public UUID getUserId() {
    return userId;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }

  @Override
  public UserCreatedEventDetails getDetails() {
    return this.details;
  }

  public static class UserCreatedEventDetails {
    private final UUID id;
    private final UUID accountId;
    private final String firstName;
    private final String lastName;
    private final String email;

    public UserCreatedEventDetails(User user) {
      this.id = user.getId();
      this.accountId = user.getAccountId();
      this.firstName = user.getFirstName();
      this.lastName = user.getLastName();
      this.email = user.getEmail();
    }

    public UUID getId() {
      return id;
    }

    public UUID getAccountId() {
      return accountId;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getEmail() {
      return email;
    }
  }
}
