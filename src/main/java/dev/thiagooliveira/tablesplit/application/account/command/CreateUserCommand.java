package dev.thiagooliveira.tablesplit.application.account.command;

public record CreateUserCommand(
    String firstName, String lastName, String email, String phone, String password) {}
