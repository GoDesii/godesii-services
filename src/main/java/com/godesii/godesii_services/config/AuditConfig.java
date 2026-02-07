package com.godesii.godesii_services.config;

import com.godesii.godesii_services.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware(){

        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (Objects.isNull(authentication)){
                    return Optional.of("System");
                }
                if (authentication.getPrincipal() instanceof UserPrincipal jwtPrincipal) {
                    return Optional.of(jwtPrincipal.getUsername());
                } else {
                    return Optional.of(authentication.getName());
                }
            }
        };
    }
}