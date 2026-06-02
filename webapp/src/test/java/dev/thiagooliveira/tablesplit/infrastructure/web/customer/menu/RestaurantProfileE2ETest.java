package dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu;

import static org.assertj.core.api.Assertions.assertThat;

import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.infrastructure.AbstractE2ESpringTest;
import dev.thiagooliveira.tablesplit.infrastructure.E2ETest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

@E2ETest
public class RestaurantProfileE2ETest extends AbstractE2ESpringTest {

  @Autowired private RestaurantRepository restaurantRepository;

  private Restaurant restaurant;

  @BeforeEach
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    restaurant = restaurantRepository.findBySlug(professionalAccount.slug()).orElseThrow();
  }

  @Test
  void testCustomerMenuLandingPage() {
    String url = getBaseUrl() + "/@" + restaurant.getSlug() + "/table/01";

    driver.get(url);

    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));

    WebElement actionBtn =
        wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a.action-btn.primary")));
    assertThat(actionBtn).isNotNull();
    assertThat(actionBtn.getAttribute("href"))
        .endsWith("/@" + restaurant.getSlug() + "/table/01/menu");

    WebElement restaurantName = driver.findElement(By.cssSelector("h1.restaurant-name"));
    assertThat(restaurantName.getText()).isEqualTo(restaurant.getName());

    WebElement statusBadge = driver.findElement(By.cssSelector("div.status-badge"));
    assertThat(statusBadge).isNotNull();
    assertThat(statusBadge.getText()).isNotBlank();

    // Language Dropdown validation
    WebElement langBtn = driver.findElement(By.cssSelector("button.lang-btn"));
    assertThat(langBtn).isNotNull();
    WebElement langDropdown = driver.findElement(By.cssSelector("div.lang-dropdown"));
    assertThat(langDropdown).isNotNull();
    List<WebElement> langOptions = langDropdown.findElements(By.cssSelector("a.lang-option"));
    assertThat(langOptions).hasSize(2);
    assertThat(langOptions.get(0).getAttribute("href")).endsWith("?lang=pt");
    assertThat(langOptions.get(1).getAttribute("href")).endsWith("?lang=en");

    WebElement category = driver.findElement(By.cssSelector("p.restaurant-category"));
    assertThat(category).isNotNull();

    WebElement tagline = driver.findElement(By.cssSelector("p.restaurant-tagline"));
    assertThat(tagline.getText()).isEqualTo(restaurant.getDescription());

    // Tags Validation
    WebElement tagsSection = driver.findElement(By.cssSelector("div.tags-section"));
    assertThat(tagsSection).isNotNull();
    List<WebElement> tags = tagsSection.findElements(By.cssSelector("div.tag"));
    assertThat(tags).hasSize(3);
    assertThat(tags.get(0).getText()).isNotBlank();
    assertThat(tags.get(1).getText()).isNotBlank();
    assertThat(tags.get(2).getText()).isNotBlank();

    // Info Cards Validation
    List<WebElement> infoCards = driver.findElements(By.cssSelector("div.info-card"));
    assertThat(infoCards.size()).isGreaterThanOrEqualTo(4);
    assertThat(infoCards.get(0).getText()).contains(restaurant.getPhone());
    assertThat(infoCards.get(1).getText()).contains(restaurant.getEmail());
    assertThat(infoCards.get(2).getText()).isNotBlank();
    assertThat(infoCards.get(3).getText()).isNotBlank();

    // Hours Validation
    WebElement hoursList = driver.findElement(By.cssSelector("div.hours-list"));
    assertThat(hoursList).isNotNull();
    List<WebElement> hoursItems = hoursList.findElements(By.cssSelector("div.hours-item"));
    assertThat(hoursItems).hasSize(7);
    for (int i = 0; i < hoursItems.size(); i++) {
      assertThat(hoursItems.get(i).getText()).isNotBlank();
    }

    // Gallery Validation
    WebElement galleryGrid = driver.findElement(By.cssSelector("div.gallery-grid"));
    assertThat(galleryGrid).isNotNull();
    List<WebElement> galleryItems = galleryGrid.findElements(By.cssSelector("div.gallery-item"));
    assertThat(galleryItems).hasSize(1);
    assertThat(galleryItems.get(0).getAttribute("data-src")).isNotBlank();

    // Map Validation
    WebElement mapContainer = driver.findElement(By.cssSelector("div.map-container"));
    assertThat(mapContainer).isNotNull();
    WebElement mapAddress = driver.findElement(By.cssSelector("p.map-address"));
    assertThat(mapAddress.getText()).contains(restaurant.getAddress());
    WebElement mapBtn = driver.findElement(By.cssSelector("div.map-info a.map-btn"));
    assertThat(mapBtn).isNotNull();
    assertThat(mapBtn.getAttribute("href")).contains("google.com/maps");
  }

  @Test
  void testCustomerMenuNavigationOnPrimaryActionClick() {
    String slug = professionalAccount.slug();
    String url = getBaseUrl() + "/@" + slug + "/table/01";

    driver.get(url);

    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(5));

    WebElement actionBtn =
        wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                By.cssSelector("a.action-btn.primary")));
    actionBtn.click();

    String expectedUrl = getBaseUrl() + "/@" + slug + "/table/01/menu";
    wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(expectedUrl));

    assertThat(driver.getCurrentUrl()).isEqualTo(expectedUrl);
  }
}
