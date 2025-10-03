package com.sparta.goatgam.global.config;

import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }
      
        if (auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return Optional.of(userDetails.getUser().getNickname());

        }

        return Optional.of("SYSTEM");
    }
}