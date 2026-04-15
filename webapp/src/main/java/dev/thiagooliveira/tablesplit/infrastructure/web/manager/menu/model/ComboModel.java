package dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model;

import dev.thiagooliveira.tablesplit.application.menu.command.CreateComboCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.UpdateComboCommand;
import dev.thiagooliveira.tablesplit.domain.menu.Combo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class ComboModel {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal comboPrice;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate endDate;

  private List<UUID> itemIds;
  private List<Integer> quantities;
  private boolean active;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getComboPrice() {
    return comboPrice;
  }

  public void setComboPrice(BigDecimal comboPrice) {
    this.comboPrice = comboPrice;
  }

  public java.time.LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(java.time.LocalDate startDate) {
    this.startDate = startDate;
  }

  public java.time.LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(java.time.LocalDate endDate) {
    this.endDate = endDate;
  }

  public List<UUID> getItemIds() {
    return itemIds;
  }

  public void setItemIds(List<UUID> itemIds) {
    this.itemIds = itemIds;
  }

  public List<Integer> getQuantities() {
    return quantities;
  }

  public void setQuantities(List<Integer> quantities) {
    this.quantities = quantities;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  private List<Combo.ComboItem> toComboItems() {
    List<Combo.ComboItem> items = new ArrayList<>();
    if (itemIds != null && quantities != null) {
      for (int i = 0; i < itemIds.size(); i++) {
        items.add(new Combo.ComboItem(itemIds.get(i), quantities.get(i)));
      }
    }
    return items;
  }

  public CreateComboCommand toCreateComboCommand() {
    return new CreateComboCommand(
        name,
        description,
        comboPrice,
        startDate != null ? startDate.atStartOfDay() : null,
        endDate != null ? endDate.atTime(java.time.LocalTime.MAX) : null,
        toComboItems(),
        active);
  }

  public UpdateComboCommand toUpdateComboCommand() {
    return new UpdateComboCommand(
        name,
        description,
        comboPrice,
        startDate != null ? startDate.atStartOfDay() : null,
        endDate != null ? endDate.atTime(java.time.LocalTime.MAX) : null,
        toComboItems(),
        active);
  }
}
