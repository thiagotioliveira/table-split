package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.menu.model.ImageModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemModel {
  private final String id;
  private final String categoryId;
  private final List<ImageModel> images;
  private final Map<String, String> name;
  private final Map<String, String> description;
  private final BigDecimal price;
  private PromotionInfo promotion;
  private final List<TagModel> tags;
  private final List<QuestionModel> questions;

  public ItemModel(Item item, String symbol) {
    this.id = item.getId().toString();
    this.categoryId = item.getCategory().getId().toString();
    this.images =
        item.getImages().stream().map(img -> new ImageModel(img.getId(), img.getName())).toList();
    this.name = convertMap(item.getName());
    this.description = convertMap(item.getDescription());
    this.price = item.getPrice();
    if (item.getPromotion() != null) {
      this.promotion =
          new PromotionInfo(
              item.getPromotion().promotionId().toString(),
              item.getPromotion().promotionalPrice(),
              item.getPromotion().discountType().name(),
              item.getPromotion().discountValue());
    }
    this.tags =
        item.getTags() != null
            ? item.getTags().stream()
                .map(
                    t -> {
                      var it =
                          dev.thiagooliveira.tablesplit.infrastructure.web.ItemTag.fromDomain(t);
                      return new TagModel(it.name(), it.getIcon(), it.getLabel());
                    })
                .toList()
            : List.of();
    this.questions = unifyQuestions(item.getQuestions());
  }

  private List<QuestionModel> unifyQuestions(
      Map<Language, List<dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion>> questionsMap) {
    if (questionsMap == null) return java.util.Collections.emptyList();
    java.util.Map<java.util.UUID, QuestionModel> unified = new java.util.LinkedHashMap<>();
    questionsMap.forEach(
        (lang, list) -> {
          list.forEach(
              q -> {
                var model = unified.computeIfAbsent(q.getId(), id -> new QuestionModel(q));
                model.getTitle().put(lang.name().toUpperCase(), q.getTitle());
                if (q.getOptions() != null) {
                  q.getOptions()
                      .forEach(
                          opt -> {
                            var optModel =
                                model.getOptions().stream()
                                    .filter(o -> o.getId().equals(opt.getId().toString()))
                                    .findFirst()
                                    .orElseGet(
                                        () -> {
                                          var o = new OptionModel(opt);
                                          model.getOptions().add(o);
                                          return o;
                                        });
                            optModel.getText().put(lang.name().toUpperCase(), opt.getText());
                          });
                }
              });
        });
    return new java.util.ArrayList<>(unified.values());
  }

  private static Map<String, String> convertMap(Map<Language, String> map) {
    return map.entrySet().stream()
        .collect(
            Collectors.toMap(entry -> entry.getKey().name().toLowerCase(), Map.Entry::getValue));
  }

  public String getId() {
    return id;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public Map<String, String> getName() {
    return name;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public List<ImageModel> getImages() {
    return images;
  }

  public PromotionInfo getPromotion() {
    return promotion;
  }

  public List<TagModel> getTags() {
    return tags;
  }

  public List<QuestionModel> getQuestions() {
    return questions;
  }

  public static class QuestionModel {
    private final String id;
    private final Map<String, String> title;
    private final String type;
    private final int min;
    private final int max;
    private final List<OptionModel> options;

    public QuestionModel(dev.thiagooliveira.tablesplit.domain.menu.ItemQuestion q) {
      this.id = q.getId().toString();
      this.title = new java.util.HashMap<>();
      this.type = q.getType().name();
      this.min = q.getMinSelections();
      this.max = q.getMaxSelections();
      this.options = new java.util.ArrayList<>();
    }

    public String getId() {
      return id;
    }

    public Map<String, String> getTitle() {
      return title;
    }

    public String getType() {
      return type;
    }

    public int getMin() {
      return min;
    }

    public int getMax() {
      return max;
    }

    public List<OptionModel> getOptions() {
      return options;
    }
  }

  public static class OptionModel {
    private final String id;
    private final Map<String, String> text;
    private final BigDecimal extraPrice;

    public OptionModel(dev.thiagooliveira.tablesplit.domain.menu.ItemOption opt) {
      this.id = opt.getId().toString();
      this.text = new java.util.HashMap<>();
      this.extraPrice = opt.getExtraPrice();
    }

    public String getId() {
      return id;
    }

    public Map<String, String> getText() {
      return text;
    }

    public BigDecimal getExtraPrice() {
      return extraPrice;
    }
  }

  public static class PromotionInfo {
    private final String promotionId;
    private final BigDecimal promotionalPrice;
    private final String discountType;
    private final BigDecimal discountValue;

    public PromotionInfo(
        String promotionId,
        BigDecimal promotionalPrice,
        String discountType,
        BigDecimal discountValue) {
      this.promotionId = promotionId;
      this.promotionalPrice = promotionalPrice;
      this.discountType = discountType;
      this.discountValue = discountValue;
    }

    public String getPromotionId() {
      return promotionId;
    }

    public BigDecimal getPromotionalPrice() {
      return promotionalPrice;
    }

    public String getDiscountType() {
      return discountType;
    }

    public BigDecimal getDiscountValue() {
      return discountValue;
    }
  }

  public static class TagModel {
    private final String name;
    private final String icon;
    private final String labelKey;

    public TagModel(String name, String icon, String labelKey) {
      this.name = name;
      this.icon = icon;
      this.labelKey = labelKey;
    }

    public String getName() {
      return name;
    }

    public String getIcon() {
      return icon;
    }

    public String getLabelKey() {
      return labelKey;
    }
  }
}
