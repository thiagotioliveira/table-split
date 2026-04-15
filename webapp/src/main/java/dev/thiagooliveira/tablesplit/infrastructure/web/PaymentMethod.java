package dev.thiagooliveira.tablesplit.infrastructure.web;

public enum PaymentMethod {
  CASH(
      "payment.method.cash",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><rect width=\"20\" height=\"12\" x=\"2\" y=\"6\" rx=\"2\"></rect><circle cx=\"12\" cy=\"12\" r=\"2\"></circle><path d=\"M6 12h.01M18 12h.01\"></path></svg>"),
  CARD(
      "payment.method.card",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><rect width=\"20\" height=\"14\" x=\"2\" y=\"5\" rx=\"2\"></rect><line x1=\"2\" x2=\"22\" y1=\"10\" y2=\"10\"></line></svg>"),
  MB_WAY(
      "payment.method.mbway",
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><path d=\"M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z\"></path></svg>");

  private final String label;
  private final String icon;

  PaymentMethod(String label, String icon) {
    this.label = label;
    this.icon = icon;
  }

  public String getLabel() {
    return label;
  }

  public String getIcon() {
    return icon;
  }
}
