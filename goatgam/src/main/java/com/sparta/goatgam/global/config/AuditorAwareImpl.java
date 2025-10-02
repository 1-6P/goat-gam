package com.sparta.goatgam.global.config;

import com.sparta.goatgam.global.security.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()){
            return Optional.of("SYSTEM");
        }
        return Optional.of(((UserDetailsImpl) auth.getPrincipal()).getUser().getNickname());
    }
}