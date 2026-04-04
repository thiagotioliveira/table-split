package dev.thiagooliveira.tablesplit.application.account.command;

public record CreateRestaurantCommand(
    String name,
    String slug,
    String description,
    String phone,
    String email,
    String website,
    String address,
    dev.thiagooliveira.tablesplit.domain.common.Currency currency,
    int serviceFee,
    int numberOfTables,
    String cuisineType,
    java.util.List<String> tags) {}
