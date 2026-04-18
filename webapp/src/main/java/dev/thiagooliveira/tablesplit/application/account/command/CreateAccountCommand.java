package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.account.Plan;
import java.time.ZoneId;

public record CreateAccountCommand(
    CreateUserCommand user, CreateRestaurantCommand restaurant, ZoneId zone, Plan plan) {}
