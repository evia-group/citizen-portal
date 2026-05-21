package com.evia.portal.serviceportal.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthConverter jwtAuthConverter;
  private final String[] permittedUrls = {
    "/api/**",
    "/v2/api-docs",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-resources",
    "/swagger-resources/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    //Disable CSRF, And enforce user authentication before accessing any resource of the API
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(authorize -> authorize
      .requestMatchers(permittedUrls).permitAll()
      .anyRequest().authenticated());

    // Point to the resource Server to use for token validation, and do not override the default config
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

    //Define the session management policy to be stateless because we're using rest api's
    http.sessionManagement(session -> session.sessionCreationPolicy(STATELESS));

    return http.build();
  }
}
