package dev.thiagooliveira.tablesplit.infrastructure.config.local;

import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.command.CreateAccountCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateRestaurantCommand;
import dev.thiagooliveira.tablesplit.application.account.command.CreateUserCommand;
import dev.thiagooliveira.tablesplit.application.menu.CreateCategory;
import dev.thiagooliveira.tablesplit.application.menu.CreateItem;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateCategoryCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.CreateItemCommand;
import dev.thiagooliveira.tablesplit.application.menu.command.ImageCommand;
import dev.thiagooliveira.tablesplit.application.order.CreateTable;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.menu.Item;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemImageEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemImageJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializerApplicationRunner implements ApplicationRunner {
  private static final List<String> images =
      List.of(
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/1.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/1.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/2.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/3.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/4.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/5.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/6.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/7.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/8.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/9.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/10.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/11.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/12.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/13.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/14.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/15.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/16.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/17.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/18.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/19.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/20.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/21.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/22.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/menu/23.jpg",
          "https://themes.pixelstrap.net/zomo/landing/frontend/assets/images/product/vp-1.png");
  private final Time time;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final CreateCategory createCategory;
  private final RestaurantRepository restaurantRepository;
  private final CreateItem createItem;
  private final CreateTable createTable;
  private final PasswordEncoder passwordEncoder;
  private final ItemImageJpaRepository imageRepository;

  public DemoDataInitializerApplicationRunner(
      Time time,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      CreateCategory createCategory,
      RestaurantRepository restaurantRepository,
      CreateItem createItem,
      CreateTable createTable,
      PasswordEncoder passwordEncoder,
      ItemImageJpaRepository imageRepository) {
    this.time = time;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.createCategory = createCategory;
    this.restaurantRepository = restaurantRepository;
    this.createItem = createItem;
    this.createTable = createTable;
    this.passwordEncoder = passwordEncoder;
    this.imageRepository = imageRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var user =
        this.transactionalContext.execute(
            () ->
                this.createAccount.execute(
                    new CreateAccountCommand(
                        new CreateUserCommand(
                            "Thiago",
                            "Oliveira",
                            "thiago@thiagoti.com",
                            "+351 963 927 988",
                            passwordEncoder.encode("Test#123"),
                            Language.PT),
                        new CreateRestaurantCommand(
                            "Restaurante Dona Maria",
                            "donamariarestaurant",
                            "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.",
                            "+351 963 927 944",
                            "contato@donamaria.com.br",
                            "https://donamaria.com.br",
                            "Rua das Flores, 123 - Centro",
                            Currency.EUR,
                            10),
                        time.getZoneId())));
    var accountId = user.getAccountId();
    var restaurant = this.restaurantRepository.findByAccountId(accountId).orElseThrow();
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "01"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "02"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "03"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "04"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "05"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "06"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "07"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "08"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "09"));
    this.transactionalContext.execute(() -> this.createTable.execute(restaurant.getId(), "10"));
    var categoryStarters =
        this.transactionalContext.execute(
            () ->
                this.createCategory.execute(
                    accountId,
                    restaurant.getId(),
                    new CreateCategoryCommand(
                        Map.of(Language.PT, "Entradas", Language.EN, "Starters"), 1)));
    var categoryMainCourse =
        this.transactionalContext.execute(
            () ->
                this.createCategory.execute(
                    accountId,
                    restaurant.getId(),
                    new CreateCategoryCommand(
                        Map.of(Language.PT, "Prato Principal", Language.EN, "Main course"), 2)));
    var categoryDesserts =
        this.transactionalContext.execute(
            () ->
                this.createCategory.execute(
                    accountId,
                    restaurant.getId(),
                    new CreateCategoryCommand(
                        Map.of(Language.PT, "Sobremesas", Language.EN, "Desserts"), 3)));
    var categoryDrinks =
        this.transactionalContext.execute(
            () ->
                this.createCategory.execute(
                    accountId,
                    restaurant.getId(),
                    new CreateCategoryCommand(
                        Map.of(Language.PT, "Bebidas", Language.EN, "Drinks"), 4)));

    // Starters
    this.transactionalContext.execute(
        () -> {
          var items = new ArrayList<Item>();
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT, "Batata-Frita Rústica", Language.EN, "Rustic French Fries"),
                      Map.of(
                          Language.PT,
                          "Batatas rústicas fritas na hora, temperadas com sal marinho e alecrim fresco.",
                          Language.EN,
                          "Freshly fried rustic potatoes, seasoned with sea salt and fresh rosemary."),
                      new BigDecimal("18.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Dadinhos de Tapioca", Language.EN, "Tapioca Dice"),
                      Map.of(
                          Language.PT,
                          "Cubos de tapioca com queijo coalho, servidos com geleia de pimenta defumada.",
                          Language.EN,
                          "Tapioca cubes with coalho cheese, served with smoked pepper jelly."),
                      new BigDecimal("22.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Coxinhas de Frango", Language.EN, "Chicken Croquettes"),
                      Map.of(
                          Language.PT,
                          "Massa crocante recheada com frango desfiado temperado e requeijão cremoso.",
                          Language.EN,
                          "Crispy dough filled with seasoned shredded chicken and creamy curd cheese."),
                      new BigDecimal("15.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Bolinho de Bacalhau", Language.EN, "Codfish Balls"),
                      Map.of(
                          Language.PT,
                          "Tradicional bolinho de bacalhau crocante e sequinho, receita da casa.",
                          Language.EN,
                          "Traditional crispy and dry codfish balls, house recipe."),
                      new BigDecimal("21.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT, "Calabresa Acebolada", Language.EN, "Sausage with Onions"),
                      Map.of(
                          Language.PT,
                          "Linguiça calabresa frita com cebolas caramelizadas e um toque de cachaça.",
                          Language.EN,
                          "Fried calabresa sausage with caramelized onions and a touch of cachaça."),
                      new BigDecimal("25.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryStarters.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Provolone à Milanesa", Language.EN, "Breaded Provolone"),
                      Map.of(
                          Language.PT,
                          "Cubos de queijo provolone empanados e fritos, servidos com mel de engenho.",
                          Language.EN,
                          "Breaded and fried provolone cheese cubes, served with sugarcane honey."),
                      new BigDecimal("28.00"))));
          saveImages(items, 0);
          return null;
        });

    // Main Courses
    this.transactionalContext.execute(
        () -> {
          var items = new ArrayList<Item>();
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Feijoada Completa", Language.EN, "Traditional Feijoada"),
                      Map.of(
                          Language.PT,
                          "A clássica feijoada com carnes nobres, arroz, couve, farofa e laranja.",
                          Language.EN,
                          "Classic black bean stew with noble meats, served with rice, kale, farofa, and orange."),
                      new BigDecimal("45.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Moqueca de Peixe", Language.EN, "Fish Moqueca"),
                      Map.of(
                          Language.PT,
                          "Cozido de peixe no leite de coco, dendê e coentro. Acompanha arroz e pirão.",
                          Language.EN,
                          "Fish stew in coconut milk, dende oil, and coriander. Served with rice and pirão."),
                      new BigDecimal("65.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Picanha na Brasa", Language.EN, "Grilled Picanha"),
                      Map.of(
                          Language.PT,
                          "Corte nobre de picanha grelhada no sal grosso, arroz biro-biro e farofa.",
                          Language.EN,
                          "Noble cut of picanha grilled with rock salt, biro-biro rice, and farofa."),
                      new BigDecimal("75.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Baião de Dois", Language.EN, "Baiao de Dois"),
                      Map.of(
                          Language.PT,
                          "Arroz com feijão-fradinho, queijo coalho, carne-seca e temperos nordestinos.",
                          Language.EN,
                          "Rice with black-eyed peas, coalho cheese, dried meat, and northeastern spices."),
                      new BigDecimal("35.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Virado à Paulista", Language.EN, "Virado a Paulista"),
                      Map.of(
                          Language.PT,
                          "Prato bandeirante com tutu de feijão, arroz, lombo, ovo frito e couve.",
                          Language.EN,
                          "Traditional state dish with bean puree, rice, pork loin, fried egg, and kale."),
                      new BigDecimal("38.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryMainCourse.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT, "Filé Oswaldo Aranha", Language.EN, "Oswaldo Aranha Steak"),
                      Map.of(
                          Language.PT,
                          "Filé alto com alho frito, arroz, farofa de ovos e batatas portuguesas.",
                          Language.EN,
                          "Thick steak with fried garlic, rice, egg farofa, and Portuguese potatoes."),
                      new BigDecimal("55.00"))));
          saveImages(items, 6);
          return null;
        });

    // Desserts
    this.transactionalContext.execute(
        () -> {
          var items = new ArrayList<Item>();
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDesserts.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Pudim de Leite", Language.EN, "Milk Pudding"),
                      Map.of(
                          Language.PT,
                          "O clássico pudim com calda de caramelo brilhante e textura aveludada.",
                          Language.EN,
                          "Classic Brazilian pudding with shiny caramel sauce and velvety texture."),
                      new BigDecimal("14.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDesserts.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT, "Mousse de Maracujá", Language.EN, "Passion Fruit Mousse"),
                      Map.of(
                          Language.PT,
                          "Refrescante mousse de maracujá com calda de sementes crocantes.",
                          Language.EN,
                          "Refreshing passion fruit mousse with crunchy seed syrup."),
                      new BigDecimal("10.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDesserts.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT, "Quindim Tradicional", Language.EN, "Traditional Quindim"),
                      Map.of(
                          Language.PT,
                          "Doce à base de gemas e coco ralado, com um brilho e cor inconfundíveis.",
                          Language.EN,
                          "Egg yolk and coconut dessert with an unmistakable shine and color."),
                      new BigDecimal("12.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDesserts.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Romeu e Julieta", Language.EN, "Romeo and Juliet"),
                      Map.of(
                          Language.PT,
                          "Fatias de goiabada cascão cremosa acompanhadas de queijo minas frescal.",
                          Language.EN,
                          "Slices of creamy guava paste accompanied by fresh Minas cheese."),
                      new BigDecimal("15.00"))));
          saveImages(items, 12);
          return null;
        });

    // Drinks
    this.transactionalContext.execute(
        () -> {
          var items = new ArrayList<Item>();
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Caipirinha de Limão", Language.EN, "Lemon Caipirinha"),
                      Map.of(
                          Language.PT,
                          "Bebida nacional preparada com cachaça, limão taiti fresco e gelo.",
                          Language.EN,
                          "National drink prepared with cachaça, fresh Tahiti lime, and ice."),
                      new BigDecimal("18.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Guaraná Antarctica", Language.EN, "Guarana Soda"),
                      Map.of(
                          Language.PT,
                          "O refrigerante original da Amazônia, com sabor único e refrescante.",
                          Language.EN,
                          "The original Amazon soda, with a unique and refreshing flavor."),
                      new BigDecimal("8.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Água de Coco", Language.EN, "Coconut Water"),
                      Map.of(
                          Language.PT,
                          "Água de coco natural e geladinha, servida diretamente na fruta.",
                          Language.EN,
                          "Natural and chilled coconut water, served directly in the fruit."),
                      new BigDecimal("12.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Chopp Gelado 300ml", Language.EN, "Chilled Beer 300ml"),
                      Map.of(
                          Language.PT,
                          "Caneca de chopp pilsen trincando, com colarinho cremoso.",
                          Language.EN,
                          "Frosty mug of pilsen beer with a creamy foam head."),
                      new BigDecimal("10.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT,
                          "Suco de Abacaxi c/ Hortelã",
                          Language.EN,
                          "Pineapple Mint Juice"),
                      Map.of(
                          Language.PT,
                          "Refrescante suco natural batido com hortelã fresca.",
                          Language.EN,
                          "Refreshing natural juice blended with fresh mint."),
                      new BigDecimal("10.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT,
                          "Cerveja Original 600ml",
                          Language.EN,
                          "Original Beer 600ml"),
                      Map.of(
                          Language.PT,
                          "Garrafa de 600ml de uma das cervejas mais tradicionais do Brasil.",
                          Language.EN,
                          "600ml bottle of one of Brazil's most traditional beers."),
                      new BigDecimal("16.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(
                          Language.PT,
                          "Caipivodka de Morango",
                          Language.EN,
                          "Strawberry Caipivodka"),
                      Map.of(
                          Language.PT,
                          "Versão da caipirinha com vodka e morangos selecionados.",
                          Language.EN,
                          "Vodka-based caipirinha version with selected strawberries."),
                      new BigDecimal("22.00"))));
          items.add(
              this.createItem.execute(
                  accountId,
                  restaurant.getId(),
                  new CreateItemCommand(
                      categoryDrinks.getId(),
                      List.of(),
                      new ImageCommand(List.of(), List.of()),
                      Map.of(Language.PT, "Café Expresso Gourmet", Language.EN, "Gourmet Espresso"),
                      Map.of(
                          Language.PT,
                          "Café expresso encorpado, moído na hora.",
                          Language.EN,
                          "Full-bodied espresso coffee, freshly ground."),
                      new BigDecimal("6.00"))));
          saveImages(items, 3);
          return null;
        });
  }

  private void saveImages(ArrayList<Item> items, int fator) {
    items.forEach(
        i -> {
          var image = new ItemImageEntity();
          image.setItemId(i.getId());
          image.setId(UUID.randomUUID());
          image.setName(images.get(items.indexOf(i) + fator));
          image.setMain(false);
          this.imageRepository.save(image);
        });
  }
}
