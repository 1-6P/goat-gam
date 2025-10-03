package com.sparta.goatgam.global.security;

import com.sparta.goatgam.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = req.getRequestURI();

        // 인증 없이 열어둘 경로
        if (uri.startsWith("/api/v1/auth/") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui")){
            filterChain.doFilter(req, res);
            return;
        }
        String tokenValue = jwtUtil.getJwtFromHeader(req);

        // 토큰이 없으면 익명으로 통과 (인가 어노테이션/설정에서 걸러짐)
        if (!StringUtils.hasText(tokenValue)) {
            filterChain.doFilter(req, res);
            return;
        }

        try {
            // 검증 1회만
            if (!jwtUtil.validateToken(tokenValue)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT Token");
                return;
            }

            String email = jwtUtil.getUserInfoFromToken(tokenValue).getSubject(); // subject=email
            setAuthentication(email); // principal = UserDetails 로 세팅

        } catch (Exception e) {
            log.warn("JWT processing error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication Failed");
            return;
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
