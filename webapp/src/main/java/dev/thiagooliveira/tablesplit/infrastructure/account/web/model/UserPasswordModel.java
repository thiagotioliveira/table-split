package dev.thiagooliveira.tablesplit.infrastructure.account.web.model;

import dev.thiagooliveira.tablesplit.application.account.command.UpdatePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordModel {
  @NotBlank private String currentPassword;

  @NotBlank
  @Size(min = 8)
  private String newPassword;

  public UserPasswordModel() {}

  public UpdatePasswordCommand toCommand() {
    return new UpdatePasswordCommand(this.newPassword);
  }

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
