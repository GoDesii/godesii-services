//package com.godesii.godesii_services.security;
//
//import com.godesii.godesii_services.repository.oauth2.AuthorizationConsentRepository;
//import com.godesii.godesii_services.repository.oauth2.AuthorizationRepository;
//import com.godesii.godesii_services.security.authentication.OAuth2PasswordAuthenticationConverter;
//import com.godesii.godesii_services.security.authentication.OAuth2PasswordAuthenticationProvider;
//import com.godesii.godesii_services.security.services.JpaOAuth2AuthorizationConsentService;
//import com.godesii.godesii_services.security.services.JpaOAuth2AuthorizationService;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.SecurityContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
//import org.springframework.security.oauth2.core.OAuth2Token;
//import org.springframework.security.oauth2.core.oidc.OidcScopes;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
//import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
//import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
//import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
//import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
//import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
//import org.springframework.security.oauth2.server.authorization.token.*;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.ExceptionTranslationFilter;
//import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
//import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//
//import javax.sql.DataSource;
//
//@Configuration(proxyBeanMethods = false)
//public class AuthorizationServerConfig {
//
//
//    private final DataSource dataSource;
//    private final AuthorizationRepository authorizationRepository;
//    private final AuthorizationConsentRepository authorizationConsentRepository;
//
//    public AuthorizationServerConfig(DataSource dataSource,
//                                     AuthorizationRepository authorizationRepository,
//                                     AuthorizationConsentRepository authorizationConsentRepository) {
//
//        this.dataSource = dataSource;
//        this.authorizationRepository = authorizationRepository;
//        this.authorizationConsentRepository = authorizationConsentRepository;
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain authorizationFilterChain(HttpSecurity http, ProviderManager manager, OAuth2TokenGenerator<OAuth2Token> tokenGenerator) throws Exception{
//
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                OAuth2AuthorizationServerConfigurer.authorizationServer();
//        http
//
//                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
//                .with(authorizationServerConfigurer, (authorizationServer) ->
//                        authorizationServer
//                                .oidc(Customizer.withDefaults())// Enable OpenID Connect 1.0
//                                .tokenEndpoint(token -> {
//                                    token.accessTokenRequestConverter(new OAuth2PasswordAuthenticationConverter());
//                                    token.authenticationProvider(new OAuth2PasswordAuthenticationProvider(
//                                           manager,
//                                           oAuth2AuthorizationService(),
//                                            tokenGenerator
//
//                                    ));
//                                    token.errorResponseHandler(new OAuth2PasswordAuthenticationFailureHandler());
//                                })
//                )
//                .authorizeHttpRequests((authorize) ->
//                        authorize
//                                .anyRequest().authenticated()
//                )
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling((exceptions) -> {
//                            exceptions.authenticationEntryPoint(loginUrlAuthenticationEntryPoint());
//                }
//                ).oauth2ResourceServer((resourceServer) -> resourceServer
//                .jwt(Customizer.withDefaults()));;
//        return http.build();
//    }
//
//    @Bean
//    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint(){
//        return new APIAuthenticationEntryPoint("/login");
//    }
//
//    @Bean // <4>
//    @SuppressWarnings("all")
//    public RegisteredClientRepository registeredClientRepository() {
//        // @formatter:off
//        RegisteredClient oidcClient = RegisteredClient.withId("oidc-client")
//                .clientId("oidc-client")
//                .clientSecret(passwordEncoder().encode("secret"))
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
//                .postLogoutRedirectUri("http://127.0.0.1:8080/")
//                .scope(OidcScopes.OPENID)
//                .scope(OidcScopes.PROFILE)
//                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
//                .build();
//        // @formatter:on
//        return new InMemoryRegisteredClientRepository(oidcClient);
//    }
//
//    @Bean // <7>
//    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
//        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
//    }
//
//    @Bean
//    public OAuth2TokenGenerator<OAuth2Token> delegatingOAuth2TokenGenerator(JwtEncoder encoder,
//                                                                            OAuth2TokenCustomizer<JwtEncodingContext> customizer) {
//        JwtGenerator generator = new JwtGenerator(encoder);
//        generator.setJwtCustomizer(customizer);
//        return new DelegatingOAuth2TokenGenerator(generator,
//                new OAuth2AccessTokenGenerator(), new OAuth2RefreshTokenGenerator());
//    }
//
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(){
//        return new JdbcTemplate(dataSource);
//    }
//
//    @Bean
//    public OAuth2AuthorizationService oAuth2AuthorizationService(){
//        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository());
//    }
////
//    @Bean
//    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(){
//        return new JpaOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository());
//    }
//
//    @Bean // <8>
//    public AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings
//                .builder()
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
//
//
//}
