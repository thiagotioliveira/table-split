package dev.thiagooliveira.tablesplit.application.restaurant.command;

import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import java.util.List;

public record UpdateRestaurantCommand(
    String name,
    String slug,
    String description,
    String website,
    String phone,
    String email,
    String address,
    CuisineType cuisineType,
    List<Tag> tags,
    List<Language> customerLanguages,
    Currency currency,
    int serviceFee,
    AveragePrice averagePrice,
    List<BusinessHours> days,
    String hashPrimaryColor,
    String hashAccentColor) {}
