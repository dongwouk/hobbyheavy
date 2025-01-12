package com.example.hobbyheavy.config;

import com.example.hobbyheavy.jwt.CustomLogoutFilter;
import com.example.hobbyheavy.jwt.JWTFilter;
import com.example.hobbyheavy.jwt.JWTUtil;
import com.example.hobbyheavy.jwt.LoginFilter;
import com.example.hobbyheavy.repository.RefreshRepository;
import com.example.hobbyheavy.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

/**
 * Spring Security 설정 클래스
 * 인증, 권한 부여, 필터 및 보안 정책을 설정.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration; // 인증 매니저 설정 객체
    private final JWTUtil jwtUtil; // JWT 유틸리티 클래스
    private final RefreshRepository refreshRepository; // Refresh 토큰 저장소
    private final UserRepository userRepository; // 사용자 저장소

    /**
     * AuthenticationManager Bean 등록
     * Spring Security의 인증 과정을 관리.
     *
     * @param configuration AuthenticationConfiguration 객체
     * @return AuthenticationManager
     * @throws Exception 예외 처리
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder Bean 등록
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain 설정
     * CORS, CSRF, 경로별 권한 설정, 필터 및 세션 관리 정책을 정의.
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 예외 처리
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용된 출처
                configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
                configuration.setAllowCredentials(true); // 쿠키 허용
                configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                configuration.setMaxAge(3600L); // 캐싱 시간 설정 (1시간)
                return configuration;
            }
        }));

        // CSRF 비활성화 (JWT 사용 시 비활성화가 일반적)
        http.csrf(csrf -> csrf.disable());

        // Form 기반 로그인 비활성화
        http.formLogin(form -> form.disable());

        // HTTP Basic 인증 비활성화
        http.httpBasic(basic -> basic.disable());

        // 경로별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/", "/join").permitAll() // 로그인, 회원가입은 인증 없이 접근 가능
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger 문서 경로
                .requestMatchers("/user/my-info").authenticated() // 인증 필요
                .requestMatchers("/user/password").authenticated() // 인증 필요
                .requestMatchers(HttpMethod.GET, "/meetup").permitAll() // GET 요청만 허용
                .requestMatchers("/admin").hasRole("ADMIN") // 관리자 권한 필요
                .requestMatchers("/reissue").permitAll() // 토큰 재발급은 인증 없이 가능
                // .requestMatchers("/schedules/**").hasAnyRole("HOST", "MEMBER") // 주석: 특정 역할만 접근 가능
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
        );

        // 세션 관리 정책: STATELESS (JWT 사용 시 세션을 사용하지 않음)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 필터 설정
        // JWTFilter를 LoginFilter 전에 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        // LoginFilter를 UsernamePasswordAuthenticationFilter 위치에 추가
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository, userRepository),
                UsernamePasswordAuthenticationFilter.class);
        // CustomLogoutFilter를 LogoutFilter 전에 추가
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        return http.build(); // SecurityFilterChain 반환
    }
}
