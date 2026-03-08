package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.vo.Language;

public record CreateRestaurantCommand(
    String name,
    String slug,
    String description,
    String phone,
    String email,
    String website,
    String address,
    Language defaultLanguage,
    String currency,
    int serviceFee) {}
