package dev.thiagooliveira.tablesplit.domain.restaurant;

import java.util.List;

public class BusinessHours {
  private String day;
  private boolean closed;
  private List<Period> periods;

  public BusinessHours() {}

  public BusinessHours(String day, boolean closed, List<Period> periods) {
    this.day = day;
    this.closed = closed;
    this.periods = periods;
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

  public List<Period> getPeriods() {
    return periods;
  }

  public void setPeriods(List<Period> periods) {
    this.periods = periods;
  }
}
