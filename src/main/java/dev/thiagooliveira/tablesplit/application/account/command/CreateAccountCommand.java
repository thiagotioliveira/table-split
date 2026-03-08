package dev.thiagooliveira.tablesplit.application.account.command;

public record CreateAccountCommand(CreateUserCommand user, CreateRestaurantCommand restaurant) {}
