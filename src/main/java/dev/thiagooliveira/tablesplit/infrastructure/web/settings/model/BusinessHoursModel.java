package dev.thiagooliveira.tablesplit.infrastructure.web.settings.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.BusinessHours;
import java.util.List;

public class BusinessHoursModel {
  private String day;
  private boolean closed;
  private List<PeriodModel> periods;

  public BusinessHoursModel() {}

  public BusinessHoursModel(BusinessHours businessHours) {
    this.day = businessHours.getDay();
    this.closed = businessHours.isClosed();
    this.periods = businessHours.getPeriods().stream().map(PeriodModel::new).toList();
  }

  public BusinessHours toCommand() {
    return new BusinessHours(
        this.day,
        this.closed,
        this.periods == null
            ? List.of()
            : this.periods.stream().map(PeriodModel::toCommand).toList());
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public List<PeriodModel> getPeriods() {
    return periods;
  }

  public void setPeriods(List<PeriodModel> periods) {
    this.periods = periods;
  }
}
