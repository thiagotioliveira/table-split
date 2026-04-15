package dev.thiagooliveira.tablesplit.application.menu.command;

import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateComboCommand(
    String name,
    String description,
    BigDecimal comboPrice,
    LocalDateTime startDate,
    LocalDateTime endDate,
    List<Combo.ComboItem> items,
    boolean active) {}
