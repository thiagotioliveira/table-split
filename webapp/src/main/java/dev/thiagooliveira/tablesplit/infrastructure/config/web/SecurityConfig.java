package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
import dev.thiagooliveira.tablesplit.infrastructure.tenant.TenantFilter;
import dev.thiagooliveira.tablesplit.infrastructure.web.Module;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
  public SecurityFilterChain securityFilterChain(HttpSecurity http, TenantFilter tenantFilter)
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
                    .migrateSession() // Protege contra fixation, migrando a sessão no login
                    .maximumSessions(1) // Permite apenas 1 sessão ativa
                    .expiredUrl(
                        "/login?expired") // URL para onde o usuário é mandado se a sessão cair
            );

    return http.build();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
