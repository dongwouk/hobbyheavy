package com.example.hobbyheavy.jwt;

import com.example.hobbyheavy.dto.request.CustomUserDetails;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.type.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로들은 JWT 필터에서 제외
        if ("/join".equals(requestURI) || "/login".equals(requestURI) || "/reissue".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWTFilter - Request URI: {}", request.getRequestURI());

        // 헤더에서 access키에 담긴 access토큰을 가져옴
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            log.warn("JWTFilter - No access token found.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token 만료 여부 확인, 만료시 다음 필터로 넘기지 않음!!
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.error("JWTFilter - Access token expired.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("invalid access token");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        if (!"access".equals(category)) {
            log.error("JWTFilter - Invalid token category.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("invalid access token");
            return;
        }

        // userId, role 값을 획득
        String userId = jwtUtil.getUserId(accessToken);
        String roleString = jwtUtil.getRole(accessToken);
        log.info("JWTFilter - UserId: {}, Role: {}", userId, roleString);

        // 열거형 Role 값으로 변환
        Role role;
        try {
            role = Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            log.error("JWTFilter - Invalid role: {}", roleString);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("invalid role");
            return;
        }

        // UserEntity 생성 및 역할 설정
        User user = User.builder()
                .userId(userId)
                .role(Collections.singleton(role))
                .build();


        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Spring Security의 Authentication 설정
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터로 넘김
        filterChain.doFilter(request, response);

    }
}
