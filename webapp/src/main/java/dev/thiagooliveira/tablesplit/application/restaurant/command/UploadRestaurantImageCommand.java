package dev.thiagooliveira.tablesplit.application.restaurant.command;

import java.util.UUID;

public record UploadRestaurantImageCommand(
    UUID accountId, UUID restaurantId, RestaurantImageDataCommand itemImageData, boolean cover) {}
