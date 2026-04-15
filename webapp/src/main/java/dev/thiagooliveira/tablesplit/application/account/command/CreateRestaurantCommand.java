package dev.thiagooliveira.tablesplit.application.account.command;

import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;

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
    CuisineType cuisineType,
    AveragePrice averagePrice,
    java.util.List<Tag> tags) {}
