package dev.thiagooliveira.tablesplit.infrastructure.web.manager.order.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ItemModel(
    UUID id,
    Map<String, String> name,
    BigDecimal price,
    UUID categoryId,
    PromotionModel promotion,
    List<QuestionModel> questions) {

  public record PromotionModel(
      UUID promotionId,
      BigDecimal promotionalPrice,
      String discountType,
      BigDecimal discountValue) {}

  public record QuestionModel(
      UUID id,
      Map<String, String> title,
      String type,
      int min,
      int max,
      boolean required,
      List<OptionModel> options) {}

  public record OptionModel(UUID id, Map<String, String> text, BigDecimal extraPrice) {}
}
