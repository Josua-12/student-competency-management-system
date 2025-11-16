package com.competency.scms.config;

import com.competency.scms.security.CustomUserDetailsService;
import com.competency.scms.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authz -> authz
                        // 공개 경로: 로그인, 비밀번호 찾기, 정적 리소스
                        .requestMatchers(
                                "/auth/**", "/login", "/logout", "/error",
                                "/favicon.ico", "/manifest.json",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/fonts/**", "/static/**"
                        ).permitAll()

                        // 인증 관련 공개 API
                        .requestMatchers("/api/user/login", "/api/user/refresh", "/api/auth/**").permitAll()

                        // 학생 전용 경로
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/mypage/**").hasRole("STUDENT")
                        .requestMatchers("/mypage").authenticated()

                        // 상담 관련 - 세분화된 권한
                        .requestMatchers("/counseling/student/**").hasRole("STUDENT")
                        .requestMatchers("/counseling/counselor/**").hasAnyRole("COUNSELOR", "COUNSELING_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/counseling/admin/**").hasAnyRole("COUNSELING_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/counseling/admin/**").hasAnyRole("COUNSELING_ADMIN", "SUPER_ADMIN")

                        // 비교과 관련 - 세분화된 권한
                        .requestMatchers("/noncurricular/student/**").hasRole("STUDENT")
                        .requestMatchers("/noncurricular/operator/**").hasAnyRole("NONCURRICULAR_OPERATOR", "NONCURRICULAR_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/noncurricular/admin/**").hasAnyRole("NONCURRICULAR_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/noncurricular/admin/**").hasAnyRole("NONCURRICULAR_ADMIN", "SUPER_ADMIN")

                        // 역량진단 관련
                        .requestMatchers("/competency/student/**").hasRole("STUDENT")
                        .requestMatchers("/competency/admin/**").hasAnyRole("COMPETENCY_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/competency/admin/**").hasAnyRole("COMPETENCY_ADMIN", "SUPER_ADMIN")

                        // 최고 관리자 전용
                        .requestMatchers("/admin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 미인증 접근 시 /auth/login로 리다이렉트
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/auth/login");
                        })
                )

                // 폼로그인 비활성 (JWT 사용)
                .formLogin(form -> form.disable())
                // 사용자 인증 프로바이더
                .authenticationProvider(authenticationProvider());

        // JWT 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
