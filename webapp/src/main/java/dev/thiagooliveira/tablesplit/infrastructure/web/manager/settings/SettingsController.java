package dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings;

import dev.thiagooliveira.tablesplit.application.restaurant.GetOrCreateToken;
import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.RegenerateToken;
import dev.thiagooliveira.tablesplit.application.restaurant.UpdateRestaurant;
import dev.thiagooliveira.tablesplit.application.restaurant.exception.SlugAlreadyExist;
import dev.thiagooliveira.tablesplit.domain.restaurant.AveragePrice;
import dev.thiagooliveira.tablesplit.domain.restaurant.Restaurant;
import dev.thiagooliveira.tablesplit.domain.restaurant.ThemeName;
import dev.thiagooliveira.tablesplit.infrastructure.security.context.AccountContext;
import dev.thiagooliveira.tablesplit.infrastructure.transactional.TransactionalContext;
import dev.thiagooliveira.tablesplit.infrastructure.web.AlertModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.ContextModel;
import dev.thiagooliveira.tablesplit.infrastructure.web.Language;
import dev.thiagooliveira.tablesplit.infrastructure.web.ManagerModule;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.RestaurantTag;
import dev.thiagooliveira.tablesplit.infrastructure.web.customer.menu.model.CuisineType;
import dev.thiagooliveira.tablesplit.infrastructure.web.manager.settings.model.SettingsModel;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@ManagerModule(Module.SETTINGS)
public class SettingsController {

  private final TransactionalContext transactionalContext;
  private final GetRestaurant getRestaurant;
  private final UpdateRestaurant updateRestaurant;
  private final GetOrCreateToken getOrCreateToken;
  private final RegenerateToken regenerateToken;
  private final MessageSource messageSource;

  public SettingsController(
      TransactionalContext transactionalContext,
      GetRestaurant getRestaurant,
      UpdateRestaurant updateRestaurant,
      GetOrCreateToken getOrCreateToken,
      RegenerateToken regenerateToken,
      MessageSource messageSource) {
    this.transactionalContext = transactionalContext;
    this.getRestaurant = getRestaurant;
    this.updateRestaurant = updateRestaurant;
    this.getOrCreateToken = getOrCreateToken;
    this.regenerateToken = regenerateToken;
    this.messageSource = messageSource;
  }

  @GetMapping
  public String index(Authentication auth, Model model) {
    ContextModel context = (ContextModel) model.getAttribute("context");
    var restaurant = getRestaurant.execute(context.getRestaurant().getId()).orElseThrow();

    String printToken =
        this.transactionalContext.execute(() -> this.getOrCreateToken.execute(restaurant.getId()));

    model.addAttribute("form", new SettingsModel(restaurant));
    model.addAttribute("languages", Language.values());
    model.addAttribute("cuisineTypeCodes", CuisineType.values());
    model.addAttribute("restaurantTags", RestaurantTag.values());
    model.addAttribute("averagePriceCodes", AveragePrice.values());
    model.addAttribute("themeNames", ThemeName.values());
    model.addAttribute("printToken", printToken);
    return "settings";
  }

  @PostMapping("/regenerate-token")
  public String regenerateToken(
      Authentication auth, Model model, RedirectAttributes redirectAttributes) {
    ContextModel context = (ContextModel) model.getAttribute("context");
    if (!context.isProfessionalOrHigher()) {
      throw new org.springframework.security.access.AccessDeniedException(
          messageSource.getMessage(
              "error.plan.feature.professional_only", null, LocaleContextHolder.getLocale()));
    }
    this.transactionalContext.execute(
        () -> regenerateToken.execute(context.getRestaurant().getId()));
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.success("alert.settings.print.token.regenerated"));
    return "redirect:/settings";
  }

  @PostMapping
  public String postSettings(
      Authentication auth,
      @Valid @ModelAttribute("form") SettingsModel form,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("languages", Language.values());
      model.addAttribute("cuisineTypeCodes", CuisineType.values());
      model.addAttribute("restaurantTags", RestaurantTag.values());
      model.addAttribute("averagePriceCodes", AveragePrice.values());
      model.addAttribute("themeNames", ThemeName.values());
      return "settings";
    }
    var context = (AccountContext) auth.getPrincipal();
    var restaurant =
        this.transactionalContext.execute(
            () -> updateRestaurant.execute(context.getRestaurant().getId(), form.toCommand()));
    redirectAttributes.addFlashAttribute("alert", AlertModel.success("alert.settings.saved"));
    updateContext(context, restaurant);
    return "redirect:/settings";
  }

  @ExceptionHandler(SlugAlreadyExist.class)
  public String handleSlugAlreadyExist(SlugAlreadyExist ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute(
        "alert", AlertModel.error("error.restaurant.slug.already.exist"));
    return "redirect:/register";
  }

  private void updateContext(AccountContext context, Restaurant restaurant) {
    context.getRestaurant().setName(restaurant.getName());
    context.getRestaurant().setCurrency(restaurant.getCurrency());
    context.getRestaurant().setCustomerLanguages(restaurant.getCustomerLanguages());
    context.getRestaurant().setDefaultLanguage(restaurant.getDefaultLanguage());
  }
}
