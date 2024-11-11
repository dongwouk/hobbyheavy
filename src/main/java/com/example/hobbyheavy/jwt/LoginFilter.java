package com.example.hobbyheavy.jwt;

import com.example.hobbyheavy.dto.response.TokenResponse;
import com.example.hobbyheavy.entity.Refresh;
import com.example.hobbyheavy.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private final Long ACCESS_EXPIRED = 600000L;
    private final Long REFRESH_EXPIRED = 86400000L; // 86400000 24 시간

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 userId, password 추출
        String userId = null;
        String password = null;

        try {
            // 요청 본문을 파싱하기 위해 ObjectMapper 사용
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);

            userId = requestMap.get("userId");
            password = requestMap.get("password");
        } catch (IOException e) {
            throw new AuthenticationException("요청 데이터를 읽을 수 없습니다.") {
            };
        }

        // Refresh 토큰이 이미 있는지 확인 (이미 로그인된 사용자인지 확인)
        if (refreshRepository.existsByUserId(userId)) {

            // Refresh 토큰 만료 확인 및 삭제 처리
            handleRefreshToken(request, response);

        }

        //스프링 시큐리티에서 userId와 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password); // 매개변수 null 추가???

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        // user info
        String userId = authentication.getName();

        // 기존 Refresh 토큰이 DB에 있다면 삭제
        refreshRepository.findByUserId(userId).ifPresent(refreshRepository::delete);

        // 사용자가 가진 모든 권한을 쉼표로 구분된 문자열로 병합하기 위한 방식
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 생성
        // 600000L 10분 , 86400000L 24시간
        String access = jwtUtil.createJwt("access", userId, role, ACCESS_EXPIRED);
        String refresh = jwtUtil.createJwt("refresh", userId, role, REFRESH_EXPIRED);

        // Refresh token DB에 저장
        addRefreshEntity(userId, refresh, REFRESH_EXPIRED);

        // 응답 DTO 생성
        TokenResponse tokenResponse = new TokenResponse(access, refresh);

        // ObjectMapper를 이용해 JSON 형태로 변환 후 응답
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);

        // 응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private void addRefreshEntity(String userId, String refresh, Long expiredMs){
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(expiredMs / 1000); // 밀리초를 초 단위로 변환
        Refresh refreshEntity = Refresh.builder()
                .userId(userId)
                .refresh(refresh)
                .expiration(expirationDate)
                .build();
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        // https 통신시 setSecure 설정
        // cookie.setSecure(true);
        // cookie 적용될 범위 설정
        //  cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    // 로그인이 refresh 토큰이 존재해서(로그인 된 상태에서 재 로그인) 실패할 경우 refresh 토큰을 삭제하는 메서드
    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken을 가져오기
        String refreshToken = jwtUtil.getJwtFromCookie(request, "refresh");

        // 쿠키에서 가져오지 못한 경우 헤더에서 refreshToken 가져오기
        if (refreshToken == null) {
            refreshToken = request.getHeader("Authorization");
        }

        if (refreshToken != null) {
            try {
                jwtUtil.isExpired(refreshToken); // 만료 확인
            } catch (ExpiredJwtException e) {
                // 만료된 경우 DB와 쿠키에서 삭제
                refreshRepository.deleteByRefresh(refreshToken);
                Cookie expiredCookie = new Cookie("refresh", null);
                expiredCookie.setMaxAge(0);
                expiredCookie.setPath("/"); // 쿠키 경로 설정
                response.addCookie(expiredCookie);
            }
        }
    }
}
