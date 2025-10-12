package com.godesii.godesii_services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception{

        http
                .securityMatchers(s->s.requestMatchers("/api/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/api/**").permitAll();
                })
                .oauth2ResourceServer(oauth2->
                        oauth2
                                .jwt(Customizer.withDefaults())
                ).exceptionHandling(ex -> ex.accessDeniedHandler(new AccessDeniedHandlerImpl()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http)
            throws Exception {
        // @formatter:off
        http
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .securityMatchers(s->s.requestMatchers("/**"))
                .authorizeHttpRequests(authorize->{
                    authorize.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
                    authorize.requestMatchers("/webjars/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico").permitAll();
                    authorize.requestMatchers("/registration", "/authenticator").hasAuthority("ROLE_MFA_REQUIRED");
                    authorize.requestMatchers("/oauth2/**","/login**","/register**","/actuator/**").permitAll();
                })
                .formLogin(Customizer.withDefaults());
        // @formatter:on

        return http.build();
    }

    @Bean // <3>
    public UserDetailsService userDetailsService() {
        // @formatter:off
        UserDetails userDetails = User.builder()
                .username("user")
                .password(passwordEncoder.encode("pass"))
                .roles("USER")
                .build();
        // @formatter:on
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(false)
                .ignoring()
                .requestMatchers("/webjars/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
