package com.knotbook.knotbook_api.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.issuer}")
    private String issuer;


    /**
     * CSRF----> disabled
     * We're building a stateless REST API that authenticates using bearer tokens rather than browser session cookies.
     * We'll disable CSRF for this API configuration.

     * SessionCreationPolicy.STATELESS
     * Spring Security will not create or maintain an HTTP session to remember the authenticated user.
     * Each protected request must provide its access token.This is different from traditional session-based authentication.

     * oauth2ResourceServer

     * This tells Spring Security to extract the bearer token and validate it using our configured JWT decoder.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers(
                                        "/api/auth/register", "/api/auth/login",
                                        "/api/auth/refresh",  "/api/auth/logout"
                                ).permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(server ->
                        server.jwt(jwt-> {}));

        return http.build();
    }

    /** HOW PASSWORD SHOULD BE ENCODED
     * BCrypt is a one-way password hashing algorithm.
     * matches() verifies whether the supplied password corresponds to the stored hash.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Our JWT will be signed using HS256.
     * - Encoder uses the key to sign the token.
     * - Decoder uses the same key to verify the token.
     * The key must remain secret. Anyone who obtains it could potentially create valid tokens.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Base64.getDecoder().decode(jwtSecret);

        return new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );
    }


    /**  HOW JWT IS GENERATED
     * This bean will be injected into our authentication service.
     * When the user logs in, we'll use it to generate the access token
     */
    @Bean
    public JwtEncoder jwtEncoder() {

        return NimbusJwtEncoder
                .withSecretKey(getSigningKey())
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    /** HOW JWT IS VALIDATED
     * Whenever Android calls a protected endpoint:
     * GET /api/projects
     * Authorization: Bearer eyJhbGciOi...

     * Spring Security:
     * 1. Extracts the JWT.
     * 2. Verifies its signature.
     * 3. Checks its expiration.
     * 4. Validates its issuer.
     * 5. Establishes the authenticated security context.
     * 6. Allows the request to continue if validation succeeds.
     * We don't manually decode tokens in every controller.
     *
     */
    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withSecretKey(getSigningKey())
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(issuer)
        );

        return decoder;
    }
}
