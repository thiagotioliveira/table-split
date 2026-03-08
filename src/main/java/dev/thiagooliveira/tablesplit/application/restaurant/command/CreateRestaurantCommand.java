package dev.thiagooliveira.tablesplit.application.restaurant.command;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import java.util.List;

public record CreateRestaurantCommand(
    String name,
    String slug,
    String description,
    String website,
    String phone,
    String email,
    String address,
    List<CuisineType> cuisineType,
    List<Tag> tags,
    Language defaultLanguage,
    List<Language> customerLanguages,
    String currency,
    int serviceFee,
    String averagePrice,
    List<BusinessHours> days,
    String hashPrimaryColor,
    String hashAccentColor) {}
