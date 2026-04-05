package dev.thiagooliveira.tablesplit.infrastructure.config.local;

import dev.thiagooliveira.tablesplit.application.account.*;
import dev.thiagooliveira.tablesplit.application.account.command.*;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.menu.*;
import dev.thiagooliveira.tablesplit.application.menu.command.*;
import dev.thiagooliveira.tablesplit.application.restaurant.RestaurantRepository;
import dev.thiagooliveira.tablesplit.domain.account.Module;
import dev.thiagooliveira.tablesplit.domain.account.User;
import dev.thiagooliveira.tablesplit.domain.common.Currency;
import dev.thiagooliveira.tablesplit.domain.common.Language;
import dev.thiagooliveira.tablesplit.domain.event.RestaurantCreatedEvent;
import dev.thiagooliveira.tablesplit.domain.menu.*;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.CuisineType;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.Tag;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemImageEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.menu.ItemImageJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestaurantImageEntity;
import dev.thiagooliveira.tablesplit.infrastructure.persistence.restautant.RestauranteImageJpaRepository;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.utils.Time;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration-test")
public class DemoDataInitializerApplicationRunner implements ApplicationRunner {

  private static final Logger logger =
      LoggerFactory.getLogger(DemoDataInitializerApplicationRunner.class);

  private static final Map<String, String> itemNamePTImageUrl =
      new HashMap<>() {
        {
          put(
              "Batata-Frita Rústica",
              "https://receitas123.com/wp-content/uploads/2023/05/batata-rustica-frita.png");
          put("Dadinhos de Tapioca", "https://f.i.uol.com.br/folha/comida/images/11290453.jpeg");
          put(
              "Coxinhas de Frango",
              "https://www.saborintenso.com/images/receitas/Coxinhas-de-Frango-SI-1.jpg");
          put(
              "Bolinho de Bacalhau",
              "https://www.aquinacozinha.com/wp-content/uploads/2025/04/bolinhos_bacalhau.png");
          put(
              "Calabresa Acebolada",
              "https://revistamenu.com.br/wp-content/uploads/sites/24/2021/05/linguiedercachaca-istock-1.jpg");
          put(
              "Provolone à Milanesa",
              "https://guiadacozinha.com.br/wp-content/uploads/2019/10/provolone-a-milanesa-50584.jpg");
          put(
              "Feijoada Completa",
              "https://canaldareceita.com.br/wp-content/uploads/2025/05/Feijoada-Completa.jpg");
          put(
              "Moqueca de Peixe",
              "https://www.leiliane.com.br/wp-content/uploads/2017/08/moqueca_baiana_1.jpg");
          put(
              "Picanha na Brasa",
              "https://media-cdn.tripadvisor.com/media/photo-s/0b/ee/2e/29/picanha-grelhada-na-brasa.jpg");
          put(
              "Baião de Dois",
              "https://prodcontent.yoki.com.br/wp-content/uploads/2024/09/Baiao-de-dois-800x450-1.jpg");
          put(
              "Virado à Paulista",
              "https://guiadacozinha.com.br/wp-content/uploads/2023/01/virado-a-paulista.jpg");
          put(
              "Filé Oswaldo Aranha",
              "https://live.staticflickr.com/65535/49938386921_29dde11650_b.jpg");
          put(
              "Pudim de Leite",
              "https://www.pingodoce.pt/wp-content/uploads/2016/10/pudimdeleite.jpg");
          put(
              "Mousse de Maracujá",
              "https://cozinha365.com.br/wp-content/uploads/2025/02/Mousse-de-Maracuja-S-500x500.webp");
          put(
              "Quindim Tradicional",
              "https://www.seara.com.br/wp-content/uploads/2025/09/quindim-tradicional-portal-minha-receita.jpg");
          put(
              "Romeu e Julieta",
              "https://receitatodahora.com.br/wp-content/uploads/2024/10/sobremesa-romeu-e-julieta-2809-1200x900.jpg");
          put(
              "Caipirinha de Limão",
              "https://i.panelinha.com.br/i1/bk-8730-blog-caipirinha-de-limao.webp");
          put(
              "Guaraná Antarctica",
              "https://www.madeinmarket.eu/cdn/shop/products/guarana-033-lata-cx-24-unidades.jpg");
          put(
              "Água de Coco",
              "https://conteudo.imguol.com.br/c/entretenimento/d7/2018/03/16/agua-de-coco-1521212503283_v2_4x3.jpg");
          put(
              "Chopp Gelado 300ml",
              "https://coppus.com.br/cdn/shop/products/29d6c13f18e2a0e7c34bd6a331591e94_grande.jpg");
          put(
              "Suco de Abacaxi c/ Hortelã",
              "https://pubimg.band.com.br/files/ef132c71be1be30ed8f2.png");
          put(
              "Cerveja Original 600ml",
              "https://http2.mlstatic.com/D_Q_NP_737048-MLU70736934268_072023-O.webp");
          put(
              "Caipivodka de Morango",
              "https://boozedrinks.pt/wp-content/uploads/2024/03/Design-sem-nome-2024-04-12T115949.946.png");
          put(
              "Café Expresso Gourmet",
              "https://upload.wikimedia.org/wikipedia/commons/2/23/Captura_de_Tela_2017-08-30_%C3%A0s_23.42.42.png");
        }
      };

  private static final List<String> restaurantImages =
      List.of(
          "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200",
          "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200",
          "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=1200",
          "https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=1200");
  private final Time time;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final CreateCategory createCategory;
  private final RestaurantRepository restaurantRepository;
  private final RestauranteImageJpaRepository restauranteImageJpaRepository;
  private final CreateItem createItem;
  private final PasswordEncoder passwordEncoder;
  private final ItemImageJpaRepository imageRepository;
  private final CreatePromotion createPromotion;
  private final CreateCombo createCombo;
  private final CreateCoupon createCoupon;
  private final CategoryRepository categoryRepository;
  private final org.springframework.context.ApplicationEventPublisher eventPublisher;
  private final CreateStaff createStaff;
  private final StaffRepository staffRepository;

  public DemoDataInitializerApplicationRunner(
      Time time,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      CreateCategory createCategory,
      RestaurantRepository restaurantRepository,
      RestauranteImageJpaRepository restauranteImageJpaRepository,
      CreateItem createItem,
      PasswordEncoder passwordEncoder,
      ItemImageJpaRepository imageRepository,
      CreatePromotion createPromotion,
      CreateCombo createCombo,
      CreateCoupon createCoupon,
      CategoryRepository categoryRepository,
      org.springframework.context.ApplicationEventPublisher eventPublisher,
      CreateStaff createStaff,
      StaffRepository staffRepository) {
    this.time = time;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.createCategory = createCategory;
    this.restaurantRepository = restaurantRepository;
    this.restauranteImageJpaRepository = restauranteImageJpaRepository;
    this.createItem = createItem;
    this.passwordEncoder = passwordEncoder;
    this.imageRepository = imageRepository;
    this.createPromotion = createPromotion;
    this.createCombo = createCombo;
    this.createCoupon = createCoupon;
    this.categoryRepository = categoryRepository;
    this.eventPublisher = eventPublisher;
    this.createStaff = createStaff;
    this.staffRepository = staffRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    User user = null;
    try {
      user =
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
                              "Cantina Brasileira",
                              "cantinabrasileira",
                              "Gastronomia brasileira de excelência, unindo tradição, qualidade e ingredientes frescos em cada detalhe do nosso cardápio.",
                              "+351 963 927 944",
                              "contato@cantinabrasileira.demo",
                              "https://cantinabrasileira.demo",
                              "Rua Conde Redondo - Lisboa",
                              Currency.EUR,
                              10,
                              10,
                              CuisineType.BRAZILIAN,
                              AveragePrice.PRICE_20_50,
                              List.of(
                                  Tag.ACCESSIBLE,
                                  Tag.WIFI,
                                  Tag.AIR_CONDITIONING,
                                  Tag.CARDS,
                                  Tag.DELIVERY,
                                  Tag.PET_FRIENDLY,
                                  Tag.RESERVATIONS)),
                          time.getZoneId())));
      logger.info("[DemoInitializer] Seeding demo data for: {}", user.getFirstName());
    } catch (UserAlreadyRegisteredException e) {
      logger.info(
          "[DemoInitializer] Demo user already registered. Skipping initial account creation.");
    }

    final Restaurant restaurant =
        this.restaurantRepository.findBySlug("cantinabrasileira").orElse(null);
    if (restaurant == null) {
      logger.info("[DemoInitializer] Restaurant not found. Stopping seeding.");
      return;
    }

    final UUID accountId = restaurant.getAccountId();

    // Check if data already seeded in this tenant
    String tenantId = TenantContext.generateTenantIdentifier(restaurant.getId());
    TenantContext.setCurrentTenant(tenantId);
    boolean alreadySeeded = false;
    try {
      final UUID rId = restaurant.getId();
      alreadySeeded =
          this.transactionalContext.execute(() -> this.categoryRepository.count(rId) > 0);
    } catch (Exception e) {
      logger.info(
          "[DemoInitializer] Schema or categories missing for restaurant: {}", restaurant.getId());
      logger.info("[DemoInitializer] Error was: {}", e.getMessage());
      logger.info("[DemoInitializer] Triggering RestaurantCreatedEvent fallback...");
      this.eventPublisher.publishEvent(new RestaurantCreatedEvent(restaurant, 10));
      logger.info("[DemoInitializer] Event published. Resetting context.");
      // Re-set context after listener just in case
      TenantContext.setCurrentTenant(tenantId);
    }

    if (alreadySeeded) {
      logger.info(
          "[DemoInitializer] Data already seeded for restaurant: {}. Skipping.",
          restaurant.getName());
      return;
    }

    // Seed Staff
    try {
      this.transactionalContext.execute(
          () -> {
            boolean exists =
                this.staffRepository.findByEmail("jose@cantinabrasileira.demo").isPresent();
            if (exists) {
              return null;
            }

            this.createStaff.execute(
                new CreateStaffCommand(
                    restaurant.getId(),
                    "José",
                    "Garçom",
                    "jose@cantinabrasileira.demo",
                    "+351 900 000 001",
                    passwordEncoder.encode("Test#123"),
                    Language.PT,
                    Set.of(Module.ORDERS, Module.TABLES)));
            return null;
          });
      logger.info("[DemoInitializer] Seeded mock staff: José Garçom");
    } catch (Exception e) {
      logger.info("[DemoInitializer] Mock staff seed error: {}", e.getMessage());
    }

    saveRestaurantImages(restaurant);

    // Set tenant context for the rest of the initialization
    dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantContext.setCurrentTenant(tenantId);

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
    List<Item> starters =
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
                              Language.PT,
                              "Batata-Frita Rústica",
                              Language.EN,
                              "Rustic French Fries"),
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
                          Map.of(
                              Language.PT, "Coxinhas de Frango", Language.EN, "Chicken Croquettes"),
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
                              Language.PT,
                              "Calabresa Acebolada",
                              Language.EN,
                              "Sausage with Onions"),
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
                          Map.of(
                              Language.PT,
                              "Provolone à Milanesa",
                              Language.EN,
                              "Breaded Provolone"),
                          Map.of(
                              Language.PT,
                              "Cubos de queijo provolone empanados e fritos, servidos com mel de engenho.",
                              Language.EN,
                              "Breaded and fried provolone cheese cubes, served with sugarcane honey."),
                          new BigDecimal("28.00"))));
              saveItemImages(items);
              return items;
            });

    // Main Courses
    List<Item> mainCourses =
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
                          Map.of(
                              Language.PT,
                              "Feijoada Completa",
                              Language.EN,
                              "Traditional Feijoada"),
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
                          Map.of(
                              Language.PT, "Virado à Paulista", Language.EN, "Virado a Paulista"),
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
                              Language.PT,
                              "Filé Oswaldo Aranha",
                              Language.EN,
                              "Oswaldo Aranha Steak"),
                          Map.of(
                              Language.PT,
                              "Filé alto com alho frito, arroz, farofa de ovos e batatas portuguesas.",
                              Language.EN,
                              "Thick steak with fried garlic, rice, egg farofa, and Portuguese potatoes."),
                          new BigDecimal("55.00"))));
              saveItemImages(items);
              return items;
            });

    // Desserts
    List<Item> desserts =
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
                              Language.PT,
                              "Mousse de Maracujá",
                              Language.EN,
                              "Passion Fruit Mousse"),
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
                              Language.PT,
                              "Quindim Tradicional",
                              Language.EN,
                              "Traditional Quindim"),
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
              saveItemImages(items);
              return items;
            });

    // Drinks
    List<Item> drinks =
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
                          Map.of(
                              Language.PT, "Caipirinha de Limão", Language.EN, "Lemon Caipirinha"),
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
                          Map.of(
                              Language.PT, "Chopp Gelado 300ml", Language.EN, "Chilled Beer 300ml"),
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
                          Map.of(
                              Language.PT,
                              "Café Expresso Gourmet",
                              Language.EN,
                              "Gourmet Espresso"),
                          Map.of(
                              Language.PT,
                              "Café expresso encorpado, moído na hora.",
                              Language.EN,
                              "Full-bodied espresso coffee, freshly ground."),
                          new BigDecimal("6.00"))));
              saveItemImages(items);
              return items;
            });

    // Seed Promotions
    this.transactionalContext.execute(
        () -> {
          // Happy Hour: 20% off all drinks, 17:00 - 20:00
          this.createPromotion.execute(
              restaurant.getId(),
              new CreatePromotionCommand(
                  "Happy Hour",
                  "20% de desconto em todas as bebidas das 17h às 20h.",
                  DiscountType.PERCENTAGE,
                  new BigDecimal("20"),
                  BigDecimal.ZERO,
                  LocalDateTime.now(),
                  LocalDateTime.now().plusMonths(3),
                  Set.of(DayOfWeek.values()),
                  LocalTime.of(17, 0),
                  LocalTime.of(20, 0),
                  ApplyType.CATEGORY,
                  Set.of(categoryDrinks.getId().toString()),
                  true));

          // Almoço Especial: 10% off main courses, Mon-Fri
          this.createPromotion.execute(
              restaurant.getId(),
              new CreatePromotionCommand(
                  "Almoço Especial",
                  "10% de desconto nos pratos principais de segunda a sexta.",
                  DiscountType.PERCENTAGE,
                  new BigDecimal("10"),
                  BigDecimal.ZERO,
                  LocalDateTime.now(),
                  LocalDateTime.now().plusMonths(3),
                  Set.of(
                      DayOfWeek.MONDAY,
                      DayOfWeek.TUESDAY,
                      DayOfWeek.WEDNESDAY,
                      DayOfWeek.THURSDAY,
                      DayOfWeek.FRIDAY),
                  null,
                  null,
                  ApplyType.CATEGORY,
                  Set.of(categoryMainCourse.getId().toString()),
                  true));

          return null;
        });

    // Seed Combos
    this.transactionalContext.execute(
        () -> {
          // Casal Perfeito: Moqueca de Peixe + 2 Caipirinhas + Pudim de Leite
          Item moqueca = mainCourses.get(1);
          Item caipirinha = drinks.get(0);
          Item pudim = desserts.get(0);

          this.createCombo.execute(
              restaurant.getId(),
              new CreateComboCommand(
                  "Combo Casal Perfeito",
                  "Moqueca de Peixe, 2 Caipirinhas e 1 Pudim de Leite.",
                  new BigDecimal("85.00"), // Original around 65 + 18*2 + 14 = 115
                  LocalDateTime.now(),
                  LocalDateTime.now().plusMonths(1),
                  List.of(
                      new Combo.ComboItem(moqueca.getId(), 1),
                      new Combo.ComboItem(caipirinha.getId(), 2),
                      new Combo.ComboItem(pudim.getId(), 1)),
                  true));

          // Entrada Mix: Dadinhos de Tapioca + Coxinhas de Frango + 2 Chopps
          Item dadinhos = starters.get(1);
          Item coxinhas = starters.get(2);
          Item chopp = drinks.get(3);

          this.createCombo.execute(
              restaurant.getId(),
              new CreateComboCommand(
                  "Mix de Entradas",
                  "Dadinhos de Tapioca, Coxinhas de Frango e 2 Chopps Gelados.",
                  new BigDecimal("45.00"), // Original around 22 + 15 + 10 * 2 = 57
                  LocalDateTime.now(),
                  LocalDateTime.now().plusMonths(1),
                  List.of(
                      new Combo.ComboItem(dadinhos.getId(), 1),
                      new Combo.ComboItem(coxinhas.getId(), 1),
                      new Combo.ComboItem(chopp.getId(), 2)),
                  true));
          return null;
        });

    // Seed Coupons
    this.transactionalContext.execute(
        () -> {
          // BEMVINDO10: 10% off
          this.createCoupon.execute(
              restaurant.getId(),
              new CreateCouponCommand(
                  "BEMVINDO10",
                  "Cupom de Boas-vindas",
                  DiscountType.PERCENTAGE,
                  new BigDecimal("10"),
                  null,
                  new BigDecimal("30"),
                  LocalDate.now().plusMonths(6),
                  100,
                  List.of(new Coupon.CouponRule(CouponRuleType.NEW_CUSTOMER, "true")),
                  true));

          // FESTA50: 50% discount on fixed value off or similar
          this.createCoupon.execute(
              restaurant.getId(),
              new CreateCouponCommand(
                  "OFF15",
                  "Desconto Especial de R$15",
                  DiscountType.FIXED_VALUE,
                  new BigDecimal("15"),
                  null,
                  new BigDecimal("50"),
                  LocalDate.now().plusMonths(1),
                  null,
                  List.of(new Coupon.CouponRule(CouponRuleType.MIN_ITEM_QUANTITY, "3")),
                  true));
          return null;
        });
  }

  private void saveRestaurantImages(Restaurant restaurant) {
    restaurantImages.stream()
        .map(
            i -> {
              var ri = new RestaurantImageEntity();
              ri.setId(UUID.randomUUID());
              ri.setName(i);
              ri.setCover(restaurantImages.indexOf(i) == 0 || restaurantImages.indexOf(i) == 1);
              ri.setRestaurantId(restaurant.getId());
              return ri;
            })
        .forEach(this.restauranteImageJpaRepository::save);
  }

  private void saveItemImages(List<Item> items) {
    items.forEach(
        item -> {
          String itemNamePT = item.getName().get(Language.PT);
          String imageUrl = itemNamePTImageUrl.get(itemNamePT);

          if (imageUrl != null) {
            var image = new ItemImageEntity();
            image.setItemId(item.getId());
            image.setId(UUID.randomUUID());
            image.setName(imageUrl);
            image.setMain(true);
            this.imageRepository.save(image);
          }
        });
  }
}
