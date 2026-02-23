package dev.thiagooliveira.tablesplit.infrastructure.web.settings.model;

import dev.thiagooliveira.tablesplit.domain.restaurant.Period;

public class PeriodModel {

  private String start;
  private String end;

  public PeriodModel() {}

  public PeriodModel(Period period) {
    this.start = period.getStart();
    this.end = period.getEnd();
  }

  public Period toCommand() {
    return new Period(this.start, this.end);
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }
}
