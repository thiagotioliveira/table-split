package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import dev.thiagooliveira.tablesplit.domain.vo.Language;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializerApplicationRunner implements ApplicationRunner {

  private final MockContext context;
  private final RestaurantJpaRepository restaurantJpaRepository;
  private final CategoryJpaRepository categoryJpaRepository;
  private final ItemJpaRepository itemJpaRepository;

  public DataInitializerApplicationRunner(
      MockContext context,
      RestaurantJpaRepository restaurantJpaRepository,
      CategoryJpaRepository categoryJpaRepository,
      ItemJpaRepository itemJpaRepository) {
    this.context = context;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.categoryJpaRepository = categoryJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var customerLanguages = List.of(Language.PT, Language.EN);
    var restaurant = new RestaurantEntity();
    restaurant.setId(UUID.randomUUID());
    restaurant.setName("Restaurante Dona Maria");
    restaurant.setSlug("donamaria.restaurant");
    restaurant.setDescription(
        "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.");
    restaurant.setWebsite("https://donamaria.com.br");
    restaurant.setPhone("+351 963 927 944");
    restaurant.setEmail("contato@donamaria.com.br");
    restaurant.setAddress("Rua das Flores, 123 - Centro");
    restaurant.getCuisineType().add(CuisineType.BRAZILIAN);
    restaurant.getTags().add(Tag.WIFI);
    restaurant.getTags().add(Tag.DELIVERY);
    restaurant.getTags().add(Tag.RESERVATIONS);
    restaurant.getTags().add(Tag.GROUPS);
    restaurant.getTags().add(Tag.CARDS);
    restaurant.setDefaultLanguage("pt-BR");
    restaurant.setCustomerLanguages(customerLanguages);
    restaurant.setCurrency("EUR");
    restaurant.setServiceFee(10);
    restaurant.setAveragePrice("20-50");
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.MONDAY.name().toLowerCase(),
                true,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.TUESDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.WEDNESDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.THURSDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.FRIDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "02:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.SATURDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "02:00"))));
    restaurant
        .getDays()
        .add(
            new BusinessHours(
                DayOfWeek.SUNDAY.name().toLowerCase(),
                false,
                List.of(new Period("11:00", "23:00"))));
    restaurant.setHashPrimaryColor("#EA580C");
    restaurant.setHashAccentColor("#FFEDD5");
    // restaurant.setHashPrimaryColor("#15803D");
    // restaurant.setHashAccentColor("#FEF9C3");
    restaurant = this.restaurantJpaRepository.save(restaurant);

    context.initContext(
        restaurant.getId(), restaurant.getName(), restaurant.getCurrency(), customerLanguages);

    // Categorias
    var catStarters = saveCategory(restaurant.getId(), 1, "Entradas", "Starters");
    var catPrincipal = saveCategory(restaurant.getId(), 2, "Prato Principal", "Main Courses");
    var catDesserts = saveCategory(restaurant.getId(), 3, "Sobremesas", "Desserts");
    var catDrinks = saveCategory(restaurant.getId(), 4, "Bebidas", "Drinks");

    // Itens - Entradas
    saveItem(
        catStarters,
        "Batata-frita",
        "French fries",
        "Batatas selecionadas, cortadas e fritas até ficarem douradas e crocantes por fora, macias por dentro. Servidas quentinhas e levemente salgadas.",
        "Selected potatoes, cut and fried until golden and crispy on the outside, soft on the inside. Served hot and lightly seasoned.",
        new BigDecimal("10.00"));

    saveItem(
        catStarters,
        "Bruschettas de Tomate",
        "Tomato Bruschettas",
        "Pão italiano tostado com tomates frescos, manjericão e azeite extra virgem.",
        "Toasted Italian bread with fresh tomatoes, basil and extra virgin olive oil.",
        new BigDecimal("15.00"));

    // Itens - Prato Principal
    saveItem(
        catPrincipal,
        "Filé Mignon ao Molho de Vinho",
        "Filet Mignon with Wine Sauce",
        "Medalhão de filé mignon grelhado, acompanhado de risoto de parmesão e molho de vinho tinto.",
        "Grilled filet mignon medallion, accompanied by parmesan risotto and red wine sauce.",
        new BigDecimal("45.00"));

    saveItem(
        catPrincipal,
        "Bacalhau à Lagareiro",
        "Codfish Lagareiro style",
        "Posta de bacalhau assada com batatas a murro, alho, cebola e azeite.",
        "Roasted cod fillet with punched potatoes, garlic, onion and olive oil.",
        new BigDecimal("38.00"));

    // Itens - Sobremesas
    saveItem(
        catDesserts,
        "Petit Gâteau com Sorvete",
        "Petit Gâteau with Ice Cream",
        "Bolinho quente de chocolate com recheio cremoso, servido com sorvete de baunilha.",
        "Warm chocolate cake with creamy filling, served with vanilla ice cream.",
        new BigDecimal("22.00"));

    saveItem(
        catDesserts,
        "Pudim de Leite Condensado",
        "Condensed Milk Pudding",
        "Pudim clássico e cremoso com calda de caramelo.",
        "Classic and creamy pudding with caramel sauce.",
        new BigDecimal("12.00"));

    // Itens - Bebidas
    saveItem(
        catDrinks,
        "Suco de Laranja Natural",
        "Natural Orange Juice",
        "Suco de laranja preparado na hora com frutas selecionadas.",
        "Freshly prepared orange juice with selected fruits.",
        new BigDecimal("8.00"));

    saveItem(
        catDrinks,
        "Vinho Tinto Reserva",
        "Reserva Red Wine",
        "Vinho tinto seco de corpo médio, perfeito para acompanhar carnes vermelhas.",
        "Medium-bodied dry red wine, perfect to accompany red meats.",
        new BigDecimal("85.00"));
  }

  private CategoryEntity saveCategory(UUID restaurantId, int order, String namePT, String nameEN) {
    var category = new CategoryEntity();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurantId);
    category.setNumOrder(order);
    category.getName().put(Language.PT, namePT);
    category.getName().put(Language.EN, nameEN);

    return this.categoryJpaRepository.save(category);
  }

  private void saveItem(
      CategoryEntity category,
      String namePT,
      String nameEN,
      String descPT,
      String descEN,
      BigDecimal price) {
    var item = new ItemEntity();
    item.setId(UUID.randomUUID());
    item.setCategory(category);
    item.getName().put(Language.PT, namePT);
    item.getName().put(Language.EN, nameEN);
    item.getDescription().put(Language.PT, descPT);
    item.getDescription().put(Language.EN, descEN);
    item.setPrice(price);

    this.itemJpaRepository.save(item);
  }
}
