package dev.thiagooliveira.tablesplit.application.restaurant;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import dev.thiagooliveira.tablesplit.domain.restaurant.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import java.util.List;

public record UpdateRestaurantCommand(
    String name,
    String description,
    String phone,
    String email,
    String address,
    List<Tag> tags,
    String defaultLanguage,
    List<Language> customerLanguages,
    String currency,
    int serviceFee,
    String averagePrice,
    List<BusinessHours> days,
    String hashPrimaryColor,
    String hashAccentColor) {}
