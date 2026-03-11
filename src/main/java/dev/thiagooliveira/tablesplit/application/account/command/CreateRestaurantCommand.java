package dev.thiagooliveira.tablesplit.application.account.command;

public record CreateRestaurantCommand(
    String name,
    String slug,
    String description,
    String phone,
    String email,
    String website,
    String address,
    String currency,
    int serviceFee) {}
