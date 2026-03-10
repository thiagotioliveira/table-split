package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import dev.thiagooliveira.tablesplit.infrastructure.security.CustomUserDetailsService;
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
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

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
                    .requestMatchers("/css/**", "/", "/login", "/register", "/h2-console/**")
                    .permitAll()

                    // Qualquer /@{algo} público
                    .requestMatchers("/@**")
                    .permitAll()

                    // Recursos protegidos
                    .requestMatchers("/menu/**", "/settings/**", "/dashboard/**")
                    .authenticated()

                    // Qualquer outra requisição exige autenticação
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true) // redirect pós login
                    .permitAll())
        .logout(
            logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

    return http.build();
  }
}
