package dev.thiagooliveira.tablesplit.infrastructure.notification.listener;

import dev.thiagooliveira.tablesplit.application.notification.EmailSender;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.infrastructure.account.event.PendingStaffPasswordCreatedEvent;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.event.PendingRegistrationCreatedEvent;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailNotificationListener {

  private final EmailSender emailSender;
  private final TemplateEngine templateEngine;
  private final MessageSource messageSource;

  public EmailNotificationListener(
      EmailSender emailSender, TemplateEngine templateEngine, MessageSource messageSource) {
    this.emailSender = emailSender;
    this.templateEngine = templateEngine;
    this.messageSource = messageSource;
  }

  @EventListener
  public void on(PendingRegistrationCreatedEvent event) {
    String verifyUrl =
        event.baseUrl()
            + "/register/verify?email="
            + java.net.URLEncoder.encode(event.email(), java.nio.charset.StandardCharsets.UTF_8);

    Locale locale = Language.toLocale(event.language());

    Context context = new Context(locale);
    context.setVariable("firstName", event.firstName());
    context.setVariable("code", event.code());
    context.setVariable("verifyUrl", verifyUrl);

    String htmlContent = templateEngine.process("mail/verification-email", context);
    String emailSubject =
        messageSource.getMessage(
            "mail.verification.subject", null, "Verify your email - TableSplit", locale);

    emailSender.sendHtmlEmail(event.email(), emailSubject, htmlContent);
  }

  @EventListener
  public void on(PendingStaffPasswordCreatedEvent event) {
    String activationUrl =
        event.baseUrl()
            + "/login-staff/set-password?token="
            + event.token()
            + "&slug="
            + event.restaurantSlug();

    Locale locale = Language.toLocale(event.defaultLanguage());

    Context context = new Context(locale);
    context.setVariable("firstName", event.firstName());
    context.setVariable("verifyUrl", activationUrl);
    context.setVariable("restaurantName", event.restaurantName());
    context.setVariable("theme", event.theme());

    String htmlContent = templateEngine.process("mail/staff-welcome-email", context);
    String emailSubject =
        messageSource.getMessage(
            "mail.staff.welcome.subject",
            new Object[] {event.restaurantName()},
            "Welcome to TableSplit! Activate your account",
            locale);

    emailSender.sendHtmlEmail(event.email(), emailSubject, htmlContent);
  }
}
