package com.reminderapp.reminder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    private final String jwtSecret;

    public AuthorizationServerConfig(@Value("{$app.jwt.secret}") String secret) {
        this.jwtSecret = secret;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        http
                .securityMatcher("/oauth2/**")
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                        )
                .csrf(csrf -> csrf.disable())
                .with(
                        OAuth2AuthorizationServerConfigurer.authorizationServer(),
                        Customizer.withDefaults()
                )
//                .exceptionHandling(exception ->
//                        exception
//                                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                                .accessDeniedHandler(new OAuth2AccessDeniedHandler())
        ;
        return http.build();
    }

//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        RegisteredClient registeredClient = RegisteredClient().withId(UUID.randomUUID().toString());
//        return new InMemoryRegisteredClientRepository(jwtSecret);
//    }
}
