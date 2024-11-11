package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Refresh;
import com.example.hobbyheavy.jwt.JWTUtil;
import com.example.hobbyheavy.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // Refresh token 획득
        String refresh = getRefreshTokenFromCookies(request);

        if (refresh == null) {
            return new ResponseEntity<>("Refresh token is missing", HttpStatus.BAD_REQUEST);
        }

        // 만료된 토큰인지 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Refresh token is expired", HttpStatus.UNAUTHORIZED);
        }

        // 토큰 타입 확인
        if (!"refresh".equals(jwtUtil.getCategory(refresh))) {
            return new ResponseEntity<>("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장된 토큰인지 확인
        if (!refreshRepository.existsByRefresh(refresh)) {
            return new ResponseEntity<>("Invalid token, not found in database", HttpStatus.BAD_REQUEST);
        }

        // 토큰 정보에서 사용자와 권한 추출
        String userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새로운 Access Token 및 Refresh Token 발급
        String newAccess = jwtUtil.createJwt("access", userId, role, 600_000L);
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 86_400_000L);

        // 기존 Refresh Token 삭제 및 새 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(userId, newRefresh, 86_400_000L);

        // 응답에 새로운 토큰 추가
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return ResponseEntity.ok("Token reissued successfully");
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private void addRefreshEntity(String userId, String refresh, Long expiredMs) {
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(expiredMs / 1000); // 밀리초를 초 단위로 변환

        Refresh refreshEntity = Refresh.builder()
                .userId(userId)
                .refresh(refresh)
                .expiration(expirationDate)
                .build();

        refreshRepository.save(refreshEntity);
        System.out.println("Refresh token saved successfully");
    }
}
