package com.godesii.godesii_services.security;

import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import com.godesii.godesii_services.repository.auth.UserRepository;
import com.godesii.godesii_services.security.management.rotation_key.RSAPrivateKeyConverter;
import com.godesii.godesii_services.security.management.rotation_key.RSAPublicKeyConverter;
import com.godesii.godesii_services.service.auth.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.NoOpAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JpaRSAKeysRepository jpaRSAKeysRepository;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver exceptionResolver;


    @Value("${app.jwt.encryptor.password}")
    private String password;
    @Value("${app.jwt.encryptor.salt}")
    private String salt;

    public SecurityConfig(JpaRSAKeysRepository jpaRSAKeysRepository, UserRepository userRepository, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jpaRSAKeysRepository = jpaRSAKeysRepository;
        this.userRepository = userRepository;
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public RSAPublicKeyConverter publicKeyConverter() {
        return new RSAPublicKeyConverter(textEncryptor());
    }

    @Bean
    public RSAPrivateKeyConverter privateKeyConverter() {
        return new RSAPrivateKeyConverter(textEncryptor());
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(jpaRSAKeysRepository, publicKeyConverter(), privateKeyConverter());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request-> {
                            request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
                            request.requestMatchers("/api/**","/error","/auth/**","/swagger-ui/**").permitAll();
                        }
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html",
                "/webjars/**");
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JwtAuthenticationFilter(jwtProvider(), userDetailsService());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailServiceImpl(userRepository);
    }

    @Bean
    public TextEncryptor textEncryptor(){
        return Encryptors.text(password, salt);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint(exceptionResolver);
    }
}
