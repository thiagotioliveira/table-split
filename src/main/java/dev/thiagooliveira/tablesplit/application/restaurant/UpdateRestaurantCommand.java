package dev.thiagooliveira.tablesplit.application.restaurant;

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
    String currency,
    int serviceFee,
    String averagePrice,
    String hashPrimaryColor,
    String hashAccentColor) {}
