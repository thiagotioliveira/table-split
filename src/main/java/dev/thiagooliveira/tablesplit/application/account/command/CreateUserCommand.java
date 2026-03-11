package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.common.Language;

public record CreateUserCommand(
    String firstName,
    String lastName,
    String email,
    String phone,
    String password,
    Language language) {}
