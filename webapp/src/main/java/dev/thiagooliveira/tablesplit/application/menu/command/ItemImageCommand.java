package dev.thiagooliveira.tablesplit.application.menu.command;

import java.util.List;
import java.util.UUID;

public record ItemImageCommand(List<UUID> imageIdsToKeep, List<ItemImageDataCommand> newImages) {}
