package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.common.Currency;

public record CreateRestaurantCommand(
    String name,
    String slug,
    String description,
    String phone,
    String email,
    String website,
    String address,
    Currency currency,
    int serviceFee) {}
