package com.godesii.godesii_services.config;

import com.godesii.godesii_services.repository.auth.JpaRSAKeysRepository;
import com.godesii.godesii_services.config.management.rotation_key.RSAPrivateKeyConverter;
import com.godesii.godesii_services.config.management.rotation_key.RSAPublicKeyConverter;
import com.godesii.godesii_services.service.auth.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JpaRSAKeysRepository jpaRSAKeysRepository;

    @Value("${app.jwt.encryptor.password}")
    private String password;
    @Value("${app.jwt.encryptor.salt}")
    private String salt;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .sessionManagement(session ->  session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        request-> {
                            request.requestMatchers("/api/**","/auth/**").permitAll();
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex-> ex.accessDeniedHandler(((request, response, accessDeniedException) -> HttpStatus.UNAUTHORIZED.toString())))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
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
        return new UserDetailServiceImpl();
    }

    @Bean
    public TextEncryptor textEncryptor(){
        return Encryptors.text(password, salt);
    }

}
