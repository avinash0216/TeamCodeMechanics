package com.example.bank.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                  ClientRegistrationRepository clientRegistrationRepository,
                                                  OAuth2AuthorizedClientService authorizedClientService,
                                                  WebClient.Builder webClientBuilder) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> {
                    // Match an explicit "application/json" Accept header, not a browser's "*/*".
                    MediaTypeRequestMatcher jsonMatcher =
                            new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON);
                    jsonMatcher.setUseEquals(true);
                    ex
                            // JSON clients (the SPA's fetch) get a clean 401.
                            .defaultAuthenticationEntryPointFor(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                    jsonMatcher)
                            // Everything else (a browser navigating to a protected URL) is
                            // redirected into the OAuth login flow.
                            .defaultAuthenticationEntryPointFor(
                                    new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/bank-auth"),
                                    AnyRequestMatcher.INSTANCE);
                })

                .oauth2Login(Customizer.withDefaults())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("BFF_SESSION")
                )

                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}