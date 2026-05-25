package dev.thiagooliveira.tablesplit.application.notification;

public interface EmailSender {
  void sendHtmlEmail(String to, String subject, String htmlContent);
}
