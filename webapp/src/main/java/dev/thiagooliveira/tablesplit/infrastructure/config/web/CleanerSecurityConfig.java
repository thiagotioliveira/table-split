package dev.thiagooliveira.tablesplit.infrastructure.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CleanerSecurityConfig {

  @Value("${app.cleaner.username}")
  private String cleanerUsername;

  @Value("${app.cleaner.password}")
  private String cleanerPassword;

  @Bean
  @Order(1) // Highest priority, evaluates this filter chain first
  public SecurityFilterChain cleanerSecurityFilterChain(
      HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
    http.securityMatcher("/api/system/cleaner/**")
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("CLEANER_SYSTEM"))
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .userDetailsService(cleanerUserDetailsService(passwordEncoder));

    return http.build();
  }

  private InMemoryUserDetailsManager cleanerUserDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails systemUser =
        User.builder()
            .username(cleanerUsername)
            .password(passwordEncoder.encode(cleanerPassword))
            .roles("CLEANER_SYSTEM")
            .build();
    return new InMemoryUserDetailsManager(systemUser);
  }
}
