package dev.thiagooliveira.tablesplit.infrastructure.web.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurantCommand;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import jakarta.validation.constraints.*;
import java.util.List;

public class SettingsModel {

  public static class LanguageForm {
    private String label;
    private String code;

    public LanguageForm() {}

    public LanguageForm(Language language) {
      this.label = language.getLabel();
      this.code = language.getCode();
    }

    public Language toCommand() {
      return new Language(this.label, this.code);
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }
  }

  public static class BusinessHoursForm {
    private String day;
    private boolean closed;
    private List<PeriodForm> periods;

    public BusinessHoursForm() {}

    public BusinessHoursForm(BusinessHours businessHours) {
      this.day = businessHours.getDay();
      this.closed = businessHours.isClosed();
      this.periods = businessHours.getPeriods().stream().map(PeriodForm::new).toList();
    }

    public BusinessHours toCommand() {
      return new BusinessHours(
          this.day,
          this.closed,
          this.periods == null
              ? List.of()
              : this.periods.stream().map(PeriodForm::toCommand).toList());
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

    public List<PeriodForm> getPeriods() {
      return periods;
    }

    public void setPeriods(List<PeriodForm> periods) {
      this.periods = periods;
    }
  }

  public static class PeriodForm {
    private String start;
    private String end;

    public PeriodForm() {}

    public PeriodForm(Period period) {
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

  public static class TagForm {

    private String icon;
    private String description;

    public TagForm() {}

    public TagForm(Tag tag) {
      this.icon = tag.getIcon();
      this.description = tag.getDescription();
    }

    public Tag toCommand() {
      return new Tag(this.icon, this.description);
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }

  public static class RestaurantForm {
    @NotBlank private String name;

    @Size(max = 254)
    private String description;

    @Size(max = 20)
    private String phone;

    @NotBlank
    @Size(max = 254)
    @Email
    private String email;

    @Size(max = 254)
    private String address;

    private List<TagForm> tags;

    @Size(max = 5)
    @NotBlank
    private String defaultLanguage;

    private List<LanguageForm> customerLanguages;

    @Size(min = 3, max = 3)
    @NotBlank
    private String currency;

    @Min(0)
    @Max(100)
    @NotNull
    private int serviceFee;

    @NotBlank
    @Size(max = 10)
    private String averagePrice;

    private List<BusinessHoursForm> days;

    @NotBlank
    @Size(min = 7, max = 7)
    private String hashPrimaryColor;

    @NotBlank
    @Size(min = 7, max = 7)
    private String hashAccentColor;

    public RestaurantForm() {}

    public RestaurantForm(Restaurant restaurant) {
      this.name = restaurant.getName();
      this.description = restaurant.getDescription();
      this.phone = restaurant.getPhone();
      this.email = restaurant.getEmail();
      this.address = restaurant.getAddress();
      this.tags =
          restaurant.getTags() == null
              ? List.of()
              : restaurant.getTags().stream().map(TagForm::new).toList();
      this.defaultLanguage = restaurant.getDefaultLanguage();
      this.customerLanguages =
          restaurant.getCustomerLanguages() == null
              ? List.of()
              : restaurant.getCustomerLanguages().stream().map(LanguageForm::new).toList();
      this.currency = restaurant.getCurrency();
      this.serviceFee = restaurant.getServiceFee();
      this.averagePrice = restaurant.getAveragePrice();
      this.days =
          restaurant.getDays() == null
              ? List.of()
              : restaurant.getDays().stream().map(BusinessHoursForm::new).toList();
      this.hashPrimaryColor = restaurant.getHashPrimaryColor();
      this.hashAccentColor = restaurant.getHashAccentColor();
    }

    public UpdateRestaurantCommand toCommand() {
      List<Tag> domainTags =
          this.tags == null ? List.of() : this.tags.stream().map(TagForm::toCommand).toList();
      List<BusinessHours> domainDays =
          this.days == null
              ? List.of()
              : this.days.stream().map(BusinessHoursForm::toCommand).toList();
      List<Language> domainLanguage =
          this.customerLanguages == null
              ? List.of()
              : this.customerLanguages.stream().map(LanguageForm::toCommand).toList();
      return new UpdateRestaurantCommand(
          this.name,
          this.description,
          this.phone,
          this.email,
          this.address,
          domainTags,
          this.defaultLanguage,
          domainLanguage,
          this.currency,
          this.serviceFee,
          this.averagePrice,
          domainDays,
          this.hashPrimaryColor,
          this.hashAccentColor);
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

    public String getPhone() {
      return phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public List<TagForm> getTags() {
      return tags;
    }

    public void setTags(List<TagForm> tags) {
      this.tags = tags;
    }

    public String getDefaultLanguage() {
      return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
      this.defaultLanguage = defaultLanguage;
    }

    public List<LanguageForm> getCustomerLanguages() {
      return customerLanguages;
    }

    public void setCustomerLanguages(List<LanguageForm> customerLanguages) {
      this.customerLanguages = customerLanguages;
    }

    public String getCurrency() {
      return currency;
    }

    public void setCurrency(String currency) {
      this.currency = currency;
    }

    public int getServiceFee() {
      return serviceFee;
    }

    public void setServiceFee(int serviceFee) {
      this.serviceFee = serviceFee;
    }

    public String getAveragePrice() {
      return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
      this.averagePrice = averagePrice;
    }

    public List<BusinessHoursForm> getDays() {
      return days;
    }

    public void setDays(List<BusinessHoursForm> days) {
      this.days = days;
    }

    public String getHashPrimaryColor() {
      return hashPrimaryColor;
    }

    public void setHashPrimaryColor(String hashPrimaryColor) {
      this.hashPrimaryColor = hashPrimaryColor;
    }

    public String getHashAccentColor() {
      return hashAccentColor;
    }

    public void setHashAccentColor(String hashAccentColor) {
      this.hashAccentColor = hashAccentColor;
    }
  }
}
