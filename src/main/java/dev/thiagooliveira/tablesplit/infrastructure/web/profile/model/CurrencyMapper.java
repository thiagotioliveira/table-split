package dev.thiagooliveira.tablesplit.infrastructure.web.profile.model;

import java.util.Map;

public class CurrencyMapper {

  private static Map<String, String> map = Map.of("EUR", "€", "BRL", "R$");

  public static String symbol(String currency) {
    return map.get(currency);
  }
}
