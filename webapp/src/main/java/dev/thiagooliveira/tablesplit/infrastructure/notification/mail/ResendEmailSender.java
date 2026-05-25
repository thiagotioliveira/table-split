package dev.thiagooliveira.tablesplit.infrastructure.notification.mail;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import dev.thiagooliveira.tablesplit.application.notification.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResendEmailSender implements EmailSender {
  private static final Logger logger = LoggerFactory.getLogger(ResendEmailSender.class);
  private final Resend resend;
  private final String apiKey;
  private final String fromEmail;

  public ResendEmailSender(
      @Value("${app.mail.resend.api-key}") String apiKey,
      @Value("${app.mail.resend.from}") String fromEmail) {
    this.apiKey = apiKey;
    this.fromEmail = fromEmail;
    if (apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("${")) {
      this.resend = new Resend(apiKey);
    } else {
      this.resend = null;
    }
  }

  @Override
  public void sendHtmlEmail(String to, String subject, String htmlContent) {
    if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("${")) {
      logger.warn(
          "[RESEND DEV MODE] Envio de e-mail simulado para: {}, Assunto: {} (conteúdo HTML omitido no console)",
          to,
          subject);
      return;
    }

    try {
      CreateEmailOptions options =
          CreateEmailOptions.builder()
              .from(fromEmail)
              .to(to)
              .subject(subject)
              .html(htmlContent)
              .build();
      resend.emails().send(options);
      logger.info("[RESEND] Email sent successfully to: {}", to);
    } catch (Exception e) {
      logger.error("[RESEND ERROR] Failed to send email to: {}", to, e);
      throw new RuntimeException("Failed to send verification email.", e);
    }
  }
}
