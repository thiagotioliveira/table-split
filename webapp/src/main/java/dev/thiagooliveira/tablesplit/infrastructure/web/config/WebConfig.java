package dev.thiagooliveira.tablesplit.infrastructure.web.config;

import dev.thiagooliveira.tablesplit.application.restaurant.GetRestaurant;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantInterceptor;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import dev.thiagooliveira.tablesplit.infrastructure.web.i18n.AppLocaleResolver;
import dev.thiagooliveira.tablesplit.infrastructure.web.security.handler.ManagerModuleHandlerInterceptor;
import java.util.Arrays;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final GetRestaurant getRestaurant;
  private final ManagerModuleHandlerInterceptor managerModuleHandlerInterceptor;
  private final TenantInterceptor tenantInterceptor;

  public WebConfig(
      GetRestaurant getRestaurant,
      ManagerModuleHandlerInterceptor managerModuleHandlerInterceptor,
      TenantInterceptor tenantInterceptor) {
    this.getRestaurant = getRestaurant;
    this.managerModuleHandlerInterceptor = managerModuleHandlerInterceptor;
    this.tenantInterceptor = tenantInterceptor;
  }

  @Bean
  public LocaleResolver localeResolver() {
    AppLocaleResolver resolver = new AppLocaleResolver(getRestaurant);
    resolver.setDefaultLocale(Locale.ENGLISH);
    return resolver;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("lang");
    return interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
    registry.addInterceptor(tenantInterceptor).addPathPatterns("/@{slug}/**");
    registry
        .addInterceptor(managerModuleHandlerInterceptor)
        .addPathPatterns(
            Arrays.stream(Module.values()).map(m -> String.format("/%s/**", m.getView())).toList());
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/sw.js")
        .addResourceLocations("classpath:/static/")
        .setCacheControl(CacheControl.noCache());

    registry
        .addResourceHandler("/**")
        .addResourceLocations("classpath:/static/")
        .setCacheControl(CacheControl.maxAge(java.time.Duration.ofDays(365)));
  }
}
