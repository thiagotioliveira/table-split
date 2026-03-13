package dev.thiagooliveira.tablesplit.application.account.command;

import java.time.ZoneId;

public record CreateAccountCommand(
    CreateUserCommand user, CreateRestaurantCommand restaurant, ZoneId zone) {}
