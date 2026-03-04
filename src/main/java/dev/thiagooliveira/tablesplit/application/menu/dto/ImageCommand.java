package dev.thiagooliveira.tablesplit.application.menu.dto;

import java.util.List;
import java.util.UUID;

public record ImageCommand(List<UUID> imageIdsToKeep, List<ImageData> newImages) {}
