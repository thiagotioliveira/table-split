package dev.thiagooliveira.tablesplit.infrastructure.web.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.thiagooliveira.tablesplit.application.account.CreateAccount;
import dev.thiagooliveira.tablesplit.application.account.exception.UserAlreadyRegisteredException;
import dev.thiagooliveira.tablesplit.application.notification.EmailSender;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.account.PendingRegistration;
import dev.thiagooliveira.tablesplit.domain.account.PendingRegistrationRepository;
import dev.thiagooliveira.tablesplit.domain.account.Plan;
import dev.thiagooliveira.tablesplit.domain.account.UserRepository;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.infrastructure.timezone.Time;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.web.login.model.RegisterModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;

@Controller
@RequestMapping("/register")
public class RegisterController {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(RegisterController.class);

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final TransactionalContext transactionalContext;
  private final CreateAccount createAccount;
  private final PendingRegistrationRepository pendingRegistrationRepository;
  private final EmailSender emailSender;
  private final TemplateEngine templateEngine;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final MessageSource messageSource;
  private final TextEncryptor textEncryptor;
  private final boolean registerEnabled;

  @Value("${app.version}")
  private String appVersion;

  public RegisterController(
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      TransactionalContext transactionalContext,
      CreateAccount createAccount,
      PendingRegistrationRepository pendingRegistrationRepository,
      EmailSender emailSender,
      TemplateEngine templateEngine,
      UserRepository userRepository,
      ObjectMapper objectMapper,
      MessageSource messageSource,
      @Value("${app.crypto.secret}") String cryptoSecret,
      @Value("${app.crypto.salt}") String cryptoSalt,
      @Value("${app.register.enabled}") boolean registerEnabled) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.transactionalContext = transactionalContext;
    this.createAccount = createAccount;
    this.pendingRegistrationRepository = pendingRegistrationRepository;
    this.emailSender = emailSender;
    this.templateEngine = templateEngine;
    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
    this.messageSource = messageSource;
    this.textEncryptor = Encryptors.text(cryptoSecret, cryptoSalt);
    this.registerEnabled = registerEnabled;
  }

  @ModelAttribute("languages")
  public Language[] languages() {
    return Language.values();
  }

  @ModelAttribute("cuisineTypeCodes")
  public CuisineType[] cuisineTypeCodes() {
    return CuisineType.values();
  }

  @ModelAttribute("restaurantTags")
  public RestaurantTag[] restaurantTags() {
    return RestaurantTag.values();
  }

  @ModelAttribute("plans")
  public Plan[] plans() {
    return new Plan[] {Plan.STARTER, Plan.PROFESSIONAL};
  }

  @ModelAttribute("maxProfessionalTables")
  public int maxProfessionalTables() {
    return Plan.PROFESSIONAL.getLimits().tables();
  }

  @ModelAttribute("averagePriceCodes")
  public AveragePrice[] averagePriceCodes() {
    return AveragePrice.values();
  }

  @ModelAttribute("appVersion")
  public String appVersion() {
    return appVersion;
  }

  @GetMapping
  public String register(Authentication auth, Model model) {
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      return "redirect:/dashboard";
    }

    if (!registerEnabled) {
      model.addAttribute("alert", AlertModel.error("error.register.enabled.false"));
    }

    model.addAttribute("form", new RegisterModel());
    return "register";
  }

  @PostMapping
  public String register(
      @Valid @ModelAttribute("form") RegisterModel form,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    if (!registerEnabled) {
      model.addAttribute("alert", AlertModel.error("error.register.enabled.false"));
      return "register";
    }
    if (bindingResult.hasErrors()) {
      model.addAttribute("alert", AlertModel.error("error.register.required.missing"));
      return "register";
    }

    if (this.userRepository.findByEmail(form.getUser().getEmail()).isPresent()) {
      model.addAttribute("alert", AlertModel.error("error.register.user.already.registered"));
      return "register";
    }

    try {
      String code = generate6DigitCode();
      java.time.LocalDateTime expiresAt = java.time.LocalDateTime.now().plusMinutes(15);
      String jsonData = objectMapper.writeValueAsString(form);
      String encryptedData = textEncryptor.encrypt(jsonData);

      var pending =
          new PendingRegistration(
              java.util.UUID.randomUUID(),
              form.getUser().getEmail().trim().toLowerCase(),
              code,
              encryptedData,
              expiresAt,
              form.getUser().getLanguage());
      this.pendingRegistrationRepository.save(pending);

      logger.debug(
          "[REGISTRATION] Codigo de verificacao gerado para {}: {}",
          form.getUser().getEmail(),
          code);

      String baseUrl =
          request
              .getRequestURL()
              .toString()
              .replace(request.getRequestURI(), request.getContextPath());
      String verifyUrl =
          baseUrl
              + "/register/verify?email="
              + java.net.URLEncoder.encode(
                  form.getUser().getEmail().trim().toLowerCase(),
                  java.nio.charset.StandardCharsets.UTF_8);

      java.util.Locale locale =
          "EN".equalsIgnoreCase(form.getUser().getLanguage())
              ? java.util.Locale.ENGLISH
              : java.util.Locale.of("pt", "PT");
      org.thymeleaf.context.Context context = new org.thymeleaf.context.Context(locale);
      context.setVariable("firstName", form.getUser().getFirstName());
      context.setVariable("code", code);
      context.setVariable("verifyUrl", verifyUrl);
      String htmlContent = templateEngine.process("mail/verification-email", context);

      String emailSubject =
          messageSource.getMessage(
              "mail.verification.subject", null, "Verify your email - TableSplit", locale);
      this.emailSender.sendHtmlEmail(form.getUser().getEmail(), emailSubject, htmlContent);

      redirectAttributes.addAttribute("email", form.getUser().getEmail());
      return "redirect:/register/verify";

    } catch (Exception e) {
      logger.error("Erro ao iniciar o processo de cadastro", e);
      model.addAttribute("alert", AlertModel.error("error.register.init"));
      return "register";
    }
  }

  @GetMapping("/verify")
  public String verifyPage(@RequestParam("email") String email, Model model) {
    model.addAttribute("email", email);
    return "verify";
  }

  @PostMapping("/verify")
  public String verifyCode(
      @RequestParam("email") String email,
      @RequestParam("code") String code,
      Model model,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    var pendingOpt = this.pendingRegistrationRepository.findByEmail(email.trim().toLowerCase());
    if (pendingOpt.isEmpty()) {
      model.addAttribute("email", email);
      model.addAttribute("alert", AlertModel.error("alert.register.pending.not.found"));
      return "verify";
    }

    var pending = pendingOpt.get();
    if (pending.isExpired()) {
      model.addAttribute("email", email);
      model.addAttribute("alert", AlertModel.error("error.register.expired"));
      return "verify";
    }

    if (!pending.getCode().equals(code.trim())) {
      model.addAttribute("email", email);
      model.addAttribute("alert", AlertModel.error("error.register.incorrect"));
      return "verify";
    }

    try {
      String decryptedData = textEncryptor.decrypt(pending.getRegistrationData());
      RegisterModel form = objectMapper.readValue(decryptedData, RegisterModel.class);

      var user =
          this.transactionalContext.execute(
              () -> this.createAccount.execute(form.toCommand(passwordEncoder, Time.getZoneId())));

      this.pendingRegistrationRepository.deleteByEmail(email.trim().toLowerCase());

      var token =
          new UsernamePasswordAuthenticationToken(user.getEmail(), form.getUser().getPassword());
      Authentication authentication = authenticationManager.authenticate(token);

      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(authentication);

      HttpSession session = request.getSession(true);
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

      return "redirect:/dashboard";

    } catch (Exception e) {
      model.addAttribute("email", email);
      model.addAttribute("alert", AlertModel.error("error.register.generic"));
      return "verify";
    }
  }

  @GetMapping("/resend")
  public String resendCode(
      @RequestParam("email") String email,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    var pendingOpt = this.pendingRegistrationRepository.findByEmail(email.trim().toLowerCase());
    if (pendingOpt.isPresent()) {
      var pending = pendingOpt.get();
      try {
        pending.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
        this.pendingRegistrationRepository.save(pending);

        logger.debug("[REGISTRATION] Verification code resent to {}: {}", email, pending.getCode());

        String decryptedData = textEncryptor.decrypt(pending.getRegistrationData());
        RegisterModel form = objectMapper.readValue(decryptedData, RegisterModel.class);

        String baseUrl =
            request
                .getRequestURL()
                .toString()
                .replace(request.getRequestURI(), request.getContextPath());
        String verifyUrl =
            baseUrl
                + "/register/verify?email="
                + java.net.URLEncoder.encode(
                    email.trim().toLowerCase(), java.nio.charset.StandardCharsets.UTF_8);

        java.util.Locale locale = Locale.forLanguageTag(pending.getLanguage());
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context(locale);
        context.setVariable("firstName", form.getUser().getFirstName());
        context.setVariable("code", pending.getCode());
        context.setVariable("verifyUrl", verifyUrl);
        String htmlContent = templateEngine.process("mail/verification-email", context);

        String emailSubject =
            messageSource.getMessage(
                "mail.verification.subject", null, "Verify your email - TableSplit", locale);
        this.emailSender.sendHtmlEmail(email, emailSubject, htmlContent);

        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.success("alert.register.code.resent"));
      } catch (Exception e) {
        redirectAttributes.addFlashAttribute(
            "alert", AlertModel.error("alert.register.code.resent.error"));
      }
    } else {
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.error("alert.register.pending.not.found"));
    }
    redirectAttributes.addAttribute("email", email);
    return "redirect:/register/verify";
  }

  private String generate6DigitCode() {
    java.util.Random random = new java.util.Random();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }

  @ExceptionHandler(UserAlreadyRegisteredException.class)
  public String handleUserAlreadyRegisteredException(
      UserAlreadyRegisteredException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.register.user.already.registered"));
    return "redirect:/register";
  }

  @ExceptionHandler(SlugAlreadyExist.class)
  public String handleSlugAlreadyExist(SlugAlreadyExist ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.restaurant.slug.already.exist"));
    return "redirect:/register";
  }
}
