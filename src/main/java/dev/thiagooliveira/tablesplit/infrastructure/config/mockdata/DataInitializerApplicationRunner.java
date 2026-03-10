package dev.thiagooliveira.tablesplit.infrastructure.config.mockdata;

import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.restaurant.*;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.account.AccountEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.account.AccountJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.account.UserEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.account.UserJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.CategoryJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantJpaRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

// @Component
public class DataInitializerApplicationRunner implements ApplicationRunner {

  private final MockContext context;
  private final AccountJpaRepository accountJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final RestaurantJpaRepository restaurantJpaRepository;
  private final CategoryJpaRepository categoryJpaRepository;
  private final ItemJpaRepository itemJpaRepository;

  public DataInitializerApplicationRunner(
      MockContext context,
      AccountJpaRepository accountJpaRepository,
      UserJpaRepository userJpaRepository,
      RestaurantJpaRepository restaurantJpaRepository,
      CategoryJpaRepository categoryJpaRepository,
      ItemJpaRepository itemJpaRepository) {
    this.context = context;
    this.accountJpaRepository = accountJpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.restaurantJpaRepository = restaurantJpaRepository;
    this.categoryJpaRepository = categoryJpaRepository;
    this.itemJpaRepository = itemJpaRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var account = new AccountEntity();
    account.setId(UUID.randomUUID());
    this.accountJpaRepository.save(account);

    var user = new UserEntity();
    user.setId(UUID.randomUUID());
    user.setFirstName("Thiago");
    user.setLastName("Oliveira");
    user.setPhone("+351 963 927 900");
    user.setEmail("thiago@thiagoti.com");
    user.setAccountId(account.getId());
    user.setPassword("Teste#123");
    this.userJpaRepository.save(user);

    var customerLanguages = List.of(Language.PT, Language.EN);
    var restaurant = new RestaurantEntity();
    restaurant.setId(UUID.randomUUID());
    restaurant.setAccountId(account.getId());
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
    restaurant.setDefaultLanguage(Language.PT);
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
        account.getId(),
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        restaurant.getId(),
        restaurant.getName(),
        restaurant.getCurrency(),
        customerLanguages);

    // Categorias
    var catStarters =
        saveCategory(
            restaurant.getId(), 1, Map.of(Language.PT, "Entradas", Language.EN, "Starters"));
    var catPrincipal =
        saveCategory(
            restaurant.getId(),
            2,
            Map.of(Language.PT, "Prato Principal", Language.EN, "Main Courses"));
    var catDesserts =
        saveCategory(
            restaurant.getId(), 3, Map.of(Language.PT, "Sobremesas", Language.EN, "Desserts"));
    var catDrinks =
        saveCategory(restaurant.getId(), 4, Map.of(Language.PT, "Bebidas", Language.EN, "Drinks"));

    // Itens - Entradas
    saveItem(
        catStarters,
        Map.of(Language.PT, "Batata-frita", Language.EN, "French fries"),
        Map.of(
            Language.PT,
            "Batatas selecionadas, cortadas e fritas até ficarem douradas e crocantes por fora, macias por dentro. Servidas quentinhas e levemente salgadas.",
            Language.EN,
            "Selected potatoes, cut and fried until golden and crispy on the outside, soft on the inside. Served hot and lightly seasoned."),
        new BigDecimal("10.00"));

    saveItem(
        catStarters,
        Map.of(
            Language.PT, "Bruschettas de Tomate"
            //                , Language.EN, "Tomato Bruschettas"
            ),
        Map.of(
            Language.PT,
            "Pão italiano tostado com tomates frescos, manjericão e azeite extra virgem.",
            Language.EN,
            "Toasted Italian bread with fresh tomatoes, basil and extra virgin olive oil."),
        new BigDecimal("15.00"));

    // Itens - Prato Principal
    saveItem(
        catPrincipal,
        Map.of(
            Language.PT,
            "Filé Mignon ao Molho de Vinho",
            Language.EN,
            "Filet Mignon with Wine Sauce"),
        Map.of(
            Language.PT,
            "Medalhão de filé mignon grelhado, acompanhado de risoto de parmesão e molho de vinho tinto.",
            Language.EN,
            "Grilled filet mignon medallion, accompanied by parmesan risotto and red wine sauce."),
        new BigDecimal("45.00"));

    saveItem(
        catPrincipal,
        Map.of(Language.PT, "Bacalhau à Lagareiro", Language.EN, "Codfish Lagareiro style"),
        Map.of(
            Language.PT,
            "Posta de bacalhau assada com batatas a murro, alho, cebola e azeite.",
            Language.EN,
            "Roasted cod fillet with punched potatoes, garlic, onion and olive oil."),
        new BigDecimal("38.00"));

    // Itens - Sobremesas
    saveItem(
        catDesserts,
        Map.of(Language.PT, "Petit Gâteau com Sorvete", Language.EN, "Petit Gâteau with Ice Cream"),
        Map.of(
            Language.PT,
            "Bolinho quente de chocolate com recheio cremoso, servido com sorvete de baunilha.",
            Language.EN,
            "Warm chocolate cake with creamy filling, served with vanilla ice cream."),
        new BigDecimal("22.00"));

    saveItem(
        catDesserts,
        Map.of(Language.PT, "Pudim de Leite Condensado", Language.EN, "Condensed Milk Pudding"),
        Map.of(
            Language.PT,
            "Pudim clássico e cremoso com calda de caramelo.",
            Language.EN,
            "Classic and creamy pudding with caramel sauce."),
        new BigDecimal("12.00"));

    // Itens - Bebidas
    saveItem(
        catDrinks,
        Map.of(Language.PT, "Suco de Laranja Natural", Language.EN, "Natural Orange Juice"),
        Map.of(
            Language.PT,
            "Suco de laranja preparado na hora com frutas selecionadas.",
            Language.EN,
            "Freshly prepared orange juice with selected fruits."),
        new BigDecimal("8.00"));

    saveItem(
        catDrinks,
        Map.of(Language.PT, "Vinho Tinto Reserva", Language.EN, "Reserva Red Wine"),
        Map.of(
            Language.PT,
            "Vinho tinto seco de corpo médio, perfeito para acompanhar carnes vermelhas.",
            Language.EN,
            "Medium-bodied dry red wine, perfect to accompany red meats."),
        new BigDecimal("85.00"));
  }

  private CategoryEntity saveCategory(UUID restaurantId, int order, Map<Language, String> name) {
    var category = new CategoryEntity();
    category.setId(UUID.randomUUID());
    category.setRestaurantId(restaurantId);
    category.setNumOrder(order);
    category.setName(name);

    return this.categoryJpaRepository.save(category);
  }

  private void saveItem(
      CategoryEntity category,
      Map<Language, String> name,
      Map<Language, String> description,
      BigDecimal price) {
    var item = new ItemEntity();
    item.setId(UUID.randomUUID());
    item.setCategory(category);
    item.setName(name);
    item.setDescription(description);
    item.setPrice(price);

    this.itemJpaRepository.save(item);
  }
}
