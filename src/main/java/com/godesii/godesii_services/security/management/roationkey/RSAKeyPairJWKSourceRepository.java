//package com.godesii.godesii_services.security.management.roationkey;
//
//import com.godesii.godesii_services.repository.oauth2.UserRepository;
//import com.godesii.godesii_services.security.management.roationkey.RSAKeyPairRepository.RsaKeyPair;
//import com.nimbusds.jose.KeySourceException;
//import com.nimbusds.jose.jwk.JWK;
//import com.nimbusds.jose.jwk.JWKSelector;
//import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.SecurityContext;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
//import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
//import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@Component
//public class RSAKeyPairJWKSourceRepository implements JWKSource<SecurityContext>, OAuth2TokenCustomizer<JwtEncodingContext> {
//
//    private final RSAKeyPairRepository rsaKeyPairRepository;
////    private final UserInfoService userInfoService;
//    private final UserRepository userRegistrationRepository;
//    private final RSAPublicKeyConverter rsaPublicKeyConverter;
//    private final RSAPrivateKeyConverter rsaPrivateKeyConverter;
//
//    public RSAKeyPairJWKSourceRepository(RSAKeyPairRepository rsaKeyPairRepository,
////                                         UserInfoService userInfoService,
//                                         UserRepository userRegistrationRepository,
//                                         RSAPublicKeyConverter rsaPublicKeyConverter,
//                                         RSAPrivateKeyConverter rsaPrivateKeyConverter) {
//        this.rsaKeyPairRepository = rsaKeyPairRepository;
////        this.userInfoService = userInfoService;
//        this.userRegistrationRepository = userRegistrationRepository;
//        this.rsaPublicKeyConverter = rsaPublicKeyConverter;
//        this.rsaPrivateKeyConverter = rsaPrivateKeyConverter;
//    }
//
//    @Override
//    public List<JWK> get(JWKSelector jwkSelector, SecurityContext context) throws KeySourceException {
//        List<RsaKeyPair> keyPairs = this.rsaKeyPairRepository.findKeyPairs();
//        List<JWK> jwks = new ArrayList<>(keyPairs.size());
//        for(RsaKeyPair keyPair: keyPairs){
//            RSAKey key = new RSAKey.Builder(keyPair.publicKey())
//                    .privateKey(keyPair.privateKey())
//                    .keyID(keyPair.id())
//                    .build();
//            if(jwkSelector.getMatcher().matches(key)){
//                jwks.add(key);
//            }
//        }
//        return jwks;
//    }
//
//    @Override
//    public void customize(JwtEncodingContext context)  {
//        Authentication principal =  context.getPrincipal();
//        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
//            Set<String> authorities = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.toSet());
//            context.getClaims().claim("authorities", authorities);
//            context.getClaims().claim("roles", Collections.singletonList("ADMIN"));
//        }
//
////        if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
////            OidcUserInfo userInfo = this.userInfoService
////                    .loadUser(context.getPrincipal().getName());
////            context
////                    .getClaims()
////                    .claims(claims ->
////                            claims.putAll(userInfo.getClaims())
////                    );
////        }
//        List<RsaKeyPair> keyPairs = this.rsaKeyPairRepository.findKeyPairs();
//        String kid = keyPairs.get(0).id();
//        context.getJwsHeader().keyId(kid);
//    }
//}
