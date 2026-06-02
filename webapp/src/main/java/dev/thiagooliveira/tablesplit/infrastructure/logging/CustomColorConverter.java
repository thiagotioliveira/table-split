package dev.thiagooliveira.tablesplit.infrastructure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class CustomColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

  @Override
  protected String getForegroundColorCode(ILoggingEvent event) {
    Level level = event.getLevel();
    switch (level.toInt()) {
      case Level.ERROR_INT:
        return ANSIConstants.RED_FG; // Vermelho
      case Level.WARN_INT:
        return ANSIConstants.YELLOW_FG; // Amarelo
      case Level.INFO_INT:
        return ANSIConstants.BLUE_FG; // Azul
      case Level.DEBUG_INT:
        return "38;5;244"; // Cinza (ANSI 256 colors) ou "90" para Bright Black
      default:
        return ANSIConstants.DEFAULT_FG;
    }
  }
}
