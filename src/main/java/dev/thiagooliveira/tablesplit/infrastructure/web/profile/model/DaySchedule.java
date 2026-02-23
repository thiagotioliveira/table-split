package dev.thiagooliveira.tablesplit.infrastructure.web.profile.model;

public class DaySchedule {
  private final String day;
  private final boolean closed;
  private final String hours;

  public DaySchedule(String day, boolean closed, String hours) {
    this.day = day;
    this.closed = closed;
    this.hours = hours;
  }

  public String getDay() {
    return day;
  }

  public boolean isClosed() {
    return closed;
  }

  public String getHours() {
    return hours;
  }
}
