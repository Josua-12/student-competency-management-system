package com.competency.scms.security;

import com.competency.scms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/auth/")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/static/")
                || "/api/user/login".equals(uri)
                || "/api/user/refresh".equals(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.debug("JWT filter skipped due to: {}", e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            if (duration > 100) {
                log.warn("JWT 토큰 검증 시간 초과: {}ms (목표: 100ms 이내)", duration);
            } else {
                log.debug("JWT 토큰 검증 완료: {}ms", duration);
            }
        }

        chain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        log.info("JWT 필터 - Authorization 헤더: {}", bearer);

        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        log.info("JWT 필터 - 쿠키 개수: {}", cookies != null ? cookies.length : 0);

        if (cookies != null) {
            for (Cookie c : cookies) {
                log.info("JWT 필터 - 쿠키: {}={}", c.getName(), c.getValue().length() > 20 ? c.getValue().substring(0, 20) + "..." : c.getValue());
                if ("accessToken".equals(c.getName())) {
                    log.info("JWT 필터 - accessToken 쿠키 발견!");
                    return c.getValue();
                }
            }
        }

        log.info("JWT 필터 - 토큰을 찾을 수 없음");
        return null;
    }
}
