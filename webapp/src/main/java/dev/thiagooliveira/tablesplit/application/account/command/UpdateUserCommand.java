package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;

public record UpdateUserCommand(
    String firstName, String lastName, String email, Language language) {}
