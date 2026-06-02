package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.assertj.core.api.Assertions.assertThat;

import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.AbstractE2ESpringTest;
import dev.thiagooliveira.tablesplit.infrastructure.E2ETest;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.menu.persistence.ItemJpaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

@E2ETest
class CustomerMenuE2ETest extends AbstractE2ESpringTest {

  @Autowired private RestaurantRepository restaurantRepository;

  @Autowired private CategoryJpaRepository categoryJpaRepository;

  @Autowired private ItemJpaRepository itemJpaRepository;

  @Autowired
  private org.springframework.transaction.support.TransactionTemplate transactionTemplate;

  @BeforeEach
  @Override
  protected void setUp() {
    super.setUp();
  }

  @Test
  @SuppressWarnings("java:S5961")
  void testCustomerTryAccessRestaurantProfileForStarterAccount() {
    var restaurant = restaurantRepository.findBySlug(starterAccount.slug()).orElseThrow();
    String url = getBaseUrl() + "/@" + restaurant.getSlug() + "/menu?lang=en";
    driver.get(url);

    new WebDriverWait(driver, java.time.Duration.ofSeconds(5));

    // Language Dropdown validation
    WebElement langBtn = driver.findElement(By.cssSelector("button.lang-btn"));
    assertThat(langBtn).isNotNull();
    WebElement langDropdown = driver.findElement(By.cssSelector("div.lang-dropdown"));
    assertThat(langDropdown).isNotNull();
    List<WebElement> langOptions = langDropdown.findElements(By.cssSelector("button.lang-option"));
    assertThat(langOptions).hasSize(2);

    WebElement currentLangFlag = driver.findElement(By.id("currentLangFlag"));
    assertThat(currentLangFlag.getText()).isEqualTo("🇬🇧");

    WebElement activeLangOption =
        langDropdown.findElement(By.cssSelector("button.lang-option.active"));
    assertThat(activeLangOption).isNotNull();
    WebElement activeFlag = activeLangOption.findElement(By.cssSelector("span.flag"));
    assertThat(activeFlag.getAttribute("innerHTML").trim()).isEqualTo("🇬🇧");

    WebElement restaurantName = driver.findElement(By.cssSelector("h1.restaurant-name"));
    assertThat(restaurantName.getText()).isEqualTo(restaurant.getName());

    WebElement statusBadge = driver.findElement(By.cssSelector("div.status-badge"));
    assertThat(statusBadge).isNotNull();
    assertThat(statusBadge.getText()).isNotBlank();

    // Fetch categories from database to validate exact values
    List<CategoryEntity> categories =
        TenentExecution.execute(
            restaurant.getId(),
            () -> {
              return transactionTemplate.execute(
                  status -> {
                    var list = categoryJpaRepository.findByRestaurantId(restaurant.getId());
                    list.forEach(
                        c -> {
                          if (c.getName() != null) {
                            c.getName().getTranslations().size();
                          }
                        });
                    return list;
                  });
            });
    List<ItemEntity> items =
        TenentExecution.execute(
            restaurant.getId(),
            () ->
                transactionTemplate.execute(
                    status -> {
                      var list =
                          itemJpaRepository.findByCategoryRestaurantIdAndDeletedAtIsNull(
                              restaurant.getId());
                      list.forEach(
                          i -> {
                            if (i.getName() != null) {
                              i.getName().getTranslations().size();
                            }
                            if (i.getDescription() != null) {
                              i.getDescription().getTranslations().size();
                            }
                          });
                      return list;
                    }));

    WebElement categoriesNav = driver.findElement(By.cssSelector("div.categories-nav"));
    assertThat(categoriesNav).isNotNull();
    List<WebElement> categoryBtns =
        categoriesNav.findElements(By.cssSelector("button.category-btn"));
    // "All" button is the first one, then one for each category
    assertThat(categoryBtns).hasSize(categories.size() + 1);

    List<WebElement> categoryTitles = driver.findElements(By.cssSelector(".category-title"));
    assertThat(categoryTitles).hasSize(categories.size());

    for (int i = 0; i < categories.size(); i++) {
      CategoryEntity category = categories.get(i);
      // Validating the title spans (they might have additional elements like count)
      assertThat(categoryTitles.get(i).getText())
          .contains(
              category
                  .getName()
                  .getTranslations()
                  .get(dev.thiagooliveira.tablesplit.domain.common.Language.EN));
    }

    List<WebElement> categoryCounts = driver.findElements(By.cssSelector(".category-count"));
    assertThat(categoryCounts).hasSize(categories.size());

    List<WebElement> menuItems = driver.findElements(By.cssSelector(".menu-item"));
    assertThat(menuItems).hasSize(items.size());

    for (int i = 0; i < items.size(); i++) {
      ItemEntity item = items.get(i);
      WebElement menuItem = menuItems.get(i);

      WebElement itemName = menuItem.findElement(By.cssSelector(".menu-item-name"));
      assertThat(itemName.getText())
          .isEqualTo(
              item.getName()
                  .getTranslations()
                  .get(dev.thiagooliveira.tablesplit.domain.common.Language.EN));

      List<WebElement> itemDescs = menuItem.findElements(By.cssSelector(".menu-item-desc"));
      if (item.getDescription() != null
          && item.getDescription()
                  .getTranslations()
                  .get(dev.thiagooliveira.tablesplit.domain.common.Language.EN)
              != null) {
        assertThat(itemDescs).isNotEmpty();
        assertThat(itemDescs.get(0).getText())
            .isEqualTo(
                item.getDescription()
                    .getTranslations()
                    .get(dev.thiagooliveira.tablesplit.domain.common.Language.EN));
      }
    }

    WebElement categoryElement = driver.findElement(By.cssSelector("p.restaurant-category"));
    assertThat(categoryElement).isNotNull();

    WebElement tagline = driver.findElement(By.cssSelector("p.restaurant-tagline"));
    assertThat(tagline.getText()).isEqualTo(restaurant.getDescription());
  }
}
