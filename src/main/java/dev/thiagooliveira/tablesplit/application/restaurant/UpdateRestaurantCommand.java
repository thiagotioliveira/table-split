package dev.thiagooliveira.tablesplit.application.restaurant;

public record UpdateRestaurantCommand(
    String name,
    String description,
    String phone,
    String email,
    String address,
    String defaultLanguage,
    String currency,
    int serviceFee,
    String averagePrice,
    String hashPrimaryColor,
    String hashAccentColor) {}
