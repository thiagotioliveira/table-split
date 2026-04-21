package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantFilter;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
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

    http.csrf(csrf -> csrf.disable()) // desabilita CSRF se for necessário (por ex. APIs)
        .headers(
            headers ->
                headers.frameOptions(
                    HeadersConfigurer.FrameOptionsConfig
                        ::sameOrigin)) // H2 console nao funciona sem isso TODO
        .authorizeHttpRequests(
            auth ->
                auth
                    // Recursos públicos
                    .requestMatchers(
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
                        "/api/notifications/**",
                        "/api/print-agent/**",
                        "/h2-console/**")
                    .permitAll()

                    // Qualquer /@{algo} público
                    .requestMatchers("/@**")
                    .permitAll()

                    // Recursos protegidos
                    .requestMatchers(
                        Arrays.stream(Module.values())
                            .filter(Module::isActive)
                            .map(m -> String.format("/%s/**", m.getView()))
                            .toArray(String[]::new))
                    .authenticated()

                    // Qualquer outra requisição exige autenticação
                    .anyRequest()
                    .authenticated())
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
