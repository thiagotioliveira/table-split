package dev.thiagooliveira.tablesplit.infrastructure.config.telegram;

import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
@Validated
public class TelegramProperties {
  private String token;
  private String username;
  private boolean enabled;
  private String baseUrl;
  private String webhookPath = "/telegram/webhook";

  @AssertTrue(
      message =
          "O token e o username do Telegram devem ser preenchidos quando o bot está habilitado.")
  public boolean isValid() {
    if (!enabled) return true;
    return token != null && !token.isBlank() && username != null && !username.isBlank();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getWebhookPath() {
    return webhookPath;
  }

  public void setWebhookPath(String webhookPath) {
    this.webhookPath = webhookPath;
  }
}
