package dev.thiagooliveira.tablesplit.application.restaurant.command;

import dev.thiagooliveira.tablesplit.application.menu.command.ImageData;
import java.util.UUID;

public record UploadRestaurantImageCommand(UUID accountId, UUID restaurantId, ImageData imageData, boolean cover) {}
