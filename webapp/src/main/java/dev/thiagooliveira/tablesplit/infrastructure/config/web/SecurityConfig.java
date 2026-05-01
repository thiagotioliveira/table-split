package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantFilter;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import jakarta.servlet.DispatcherType;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final Environment env;

  public SecurityConfig(Environment env) {
    this.env = env;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(
      HttpSecurity http,
      PasswordEncoder passwordEncoder,
      CustomUserDetailsService userDetailsService)
      throws Exception {
    DaoAuthenticationProvider authenticationProvider =
        new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  @Order(1)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/system/**")
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .httpBasic(
            httpBasic ->
                httpBasic
                    .realmName("TableSplit API")
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http, TenantFilter tenantFilter)
      throws Exception {

    // Configuração de Headers baseada em perfil
    http.headers(
        headers -> {
          if (env.acceptsProfiles(Profiles.of("h2"))) {
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
          }
        });

    // Configuração de CSRF baseada em perfil
    http.csrf(
        csrf -> {
          if (env.acceptsProfiles(Profiles.of("prod"))) {
            csrf.ignoringRequestMatchers("/api/print-agent/**");
            if (env.acceptsProfiles(Profiles.of("h2"))) {
              csrf.ignoringRequestMatchers("/h2-console/**");
            }
          } else {
            csrf.disable();
          }
        });

    http.authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(
                      "/",
                      "/actuator/health",
                      "/actuator/info",
                      "/login",
                      "/forgot-password",
                      "/login-staff",
                      "/register",
                      "/css/**",
                      "/media/**",
                      "/js/**",
                      "/pwa-install.js",
                      "/manifest.json",
                      "/sw.js",
                      "/favicon.ico",
                      "/images/**",
                      "/@**",
                      "/@**/**",
                      "/api/print-agent/**",
                      "/api/notifications/sse/subscribe/**",
                      "/api/notifications/push/public-key",
                      "/api/v1/customer/**")
                  .permitAll()
                  .dispatcherTypeMatchers(DispatcherType.ERROR)
                  .permitAll();

              auth.requestMatchers("/api/v1/manager/**").authenticated();

              if (env.acceptsProfiles(Profiles.of("h2"))) {
                auth.requestMatchers("/h2-console/**").permitAll();
              }

              auth.requestMatchers(
                      Arrays.stream(Module.values())
                          .filter(Module::isActive)
                          .map(m -> String.format("/%s/**", m.getView()))
                          .toArray(String[]::new))
                  .authenticated()
                  .anyRequest()
                  .authenticated();
            })
        .formLogin(
            form ->
                form.loginPage("/login")
                    .failureHandler(
                        (request, response, exception) -> {
                          request
                              .getSession()
                              .setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
                          String slug = request.getParameter("slug");
                          if (slug != null && !slug.isEmpty()) {
                            response.sendRedirect("/login-staff?error&slug=" + slug);
                          } else {
                            response.sendRedirect("/login?error");
                          }
                        })
                    .defaultSuccessUrl("/dashboard", true) // redirect pós login
                    .permitAll())
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
        .addFilterAfter(
            tenantFilter,
            org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class)
        .sessionManagement(
            session ->
                session
                    .sessionFixation()
                    .migrateSession()
                    .maximumSessions(1)
                    .expiredUrl("/login?expired"))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"))
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

    return http.build();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
